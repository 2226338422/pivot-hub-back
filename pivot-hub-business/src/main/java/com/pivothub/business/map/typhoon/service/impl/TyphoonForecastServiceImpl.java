package com.pivothub.business.map.typhoon.service.impl;

import com.alibaba.fastjson2.JSON;
import com.pivothub.business.map.typhoon.service.TyphoonForecastService;
import com.pivothub.business.map.typhoon.service.TyphoonService;
import com.pivothub.business.map.typhoon.utils.TyphoonProvinceForecast;
import com.pivothub.common.feign.SystemConfigClient;
import com.pivothub.common.feign.SystemMessageTemplateClient;
import com.pivothub.common.feign.SystemUserClient;
import com.pivothub.common.notify.mq.NotifyMqProducer;
import com.pivothub.common.notify.template.MessageTemplateRenderer;
import com.pivothub.common.schedule.ScheduledTaskExecutionResult;
import com.pivothub.commoncore.constants.redis.BusinessRedisConstants;
import com.pivothub.commoncore.constants.system.ScheduledTaskConstants;
import com.pivothub.commoncore.enums.NotifyMessageType;
import com.pivothub.pojo.dto.business.map.typhoon.TyphoonProvinceForecastResultDto;
import com.pivothub.pojo.dto.business.map.typhoon.TyphoonProvinceMatchDto;
import com.pivothub.pojo.dto.system.notify.MessageTemplateQueryDto;
import com.pivothub.pojo.dto.system.notify.NotifyEventDto;
import com.pivothub.pojo.vo.business.map.typhoon.TyphoonListVo;
import com.pivothub.pojo.vo.business.map.typhoon.TyphoonSummaryVo;
import com.pivothub.pojo.vo.system.notify.MessageTemplateVo;
import com.pivothub.pojo.vo.system.UserProvinceVo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.format.ResolverStyle;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.TreeSet;

import static com.pivothub.commoncore.constants.system.SystemSettingsConstants.TYPHOON_FORECAST_QUERY_TIME_KEY;

@Service
public class TyphoonForecastServiceImpl implements TyphoonForecastService {
    private static final Logger LOG = LoggerFactory.getLogger(TyphoonForecastServiceImpl.class);
    private static final String MESSAGE_TYPE = NotifyMessageType.TYPHOON.getValue();
    private static final int MAX_ERROR_LENGTH = 500;
    private static final ZoneId CHINA_ZONE = ZoneId.of("Asia/Shanghai");
    private static final DateTimeFormatter CONFIG_TIME_FORMAT = DateTimeFormatter
            .ofPattern("uuuu-MM-dd HH:mm:ss").withResolverStyle(ResolverStyle.STRICT);

    @Autowired
    private TyphoonService typhoonService;
    @Autowired
    private TyphoonProvinceForecast provinceForecast;
    @Autowired
    private NotifyMqProducer notifyMqProducer;
    @Lazy
    @Autowired
    private SystemMessageTemplateClient templateClient;
    @Lazy
    @Autowired
    private SystemConfigClient configClient;
    @Lazy
    @Autowired
    private SystemUserClient systemUserClient;

    @Override
    public String taskCode() {
        return ScheduledTaskConstants.TYPHOON_FORECAST_TASK_CODE;
    }

    @Override
    public String lockKey() {
        return BusinessRedisConstants.MAP_TYPHOON_FORECAST_LOCK_KEY;
    }

    @Override
    public ScheduledTaskExecutionResult execute(String runId, Instant startedAt) {
        Map<String, Object> result = new LinkedHashMap<>();
        List<String> errors = new ArrayList<>();
        String resultSummary = "任务执行失败";
        try {
            Instant forecastAt = forecastTime(startedAt);
            TyphoonListVo list = typhoonService.list(forecastAt);
            result.put("forecastAt", forecastAt);
            List<TyphoonSummaryVo> activeStorms = activeStorms(list.getTyphoons());
            resultSummary = activeStorms.isEmpty() ? "无活跃台风" : activeStorms.size() + "个活跃台风";
            result.put("stale", list.isStale());
            result.put("fetchedAt", list.getFetchedAt());
            result.put("activeStorms", activeStorms);
            result.put("forecastMatches", Map.of());

            Map<String, Set<String>> groups = Map.of();
            if (!activeStorms.isEmpty()) {
                List<UserProvinceVo> recipients = systemUserClient.listUserProvinces();
                Map<List<String>, Set<String>> recipientsByPreference = groupByProvincePreference(recipients);
                Set<String> provinces = new TreeSet<>();
                recipientsByPreference.keySet().forEach(provinces::addAll);
                // 从本次执行时间起检查预测路径是否经过用户所选省份。
                TyphoonProvinceForecastResultDto forecast =
                        provinceForecast.match(activeStorms, provinces, startedAt, forecastAt);
                result.put("forecastMatches", forecast.getMatches());
                result.put("unavailableForecastStormIds", forecast.getUnavailableStormIds());
                result.put("unavailableProvinceCodes", forecast.getUnavailableProvinceCodes());
                if (!forecast.getUnavailableStormIds().isEmpty() || !forecast.getUnavailableProvinceCodes().isEmpty()) {
                    errors.add("部分台风路径或省级边界不可用");
                }
                groups = buildNotificationGroups(activeStorms, recipientsByPreference, forecast.getMatches());
            }
            for (Map.Entry<String, Set<String>> group : groups.entrySet()) {
                try {
                    send(runId, startedAt, group.getKey(), group.getValue());
                } catch (RuntimeException failed) {
                    if (!errors.contains("台风提醒发送失败")) {
                        errors.add("台风提醒发送失败");
                    }
                    LOG.warn("台风预报提醒发送失败 runId={}", runId, failed);
                }
            }
        } catch (RuntimeException failed) {
            LOG.warn("台风预报任务执行失败 runId={}", runId, failed);
            String reason = failed.getMessage();
            errors.add(reason == null || reason.isBlank() ? failed.getClass().getSimpleName() : reason);
        }
        String error = errors.isEmpty() ? null : String.join("；", errors);
        if (error != null && error.length() > MAX_ERROR_LENGTH) {
            error = error.substring(0, MAX_ERROR_LENGTH);
        }
        ScheduledTaskExecutionResult executionResult = new ScheduledTaskExecutionResult();
        executionResult.setResultSummary(resultSummary);
        executionResult.setResultJson(JSON.toJSONString(result));
        executionResult.setErrorMessage(error);
        return executionResult;
    }

    private Instant forecastTime(Instant startedAt) {
        String configured = configClient.getConfigValue(TYPHOON_FORECAST_QUERY_TIME_KEY);
        if (configured == null || configured.isBlank()) {
            return startedAt;
        }
        try {
            return LocalDateTime.parse(configured.trim(), CONFIG_TIME_FORMAT).atZone(CHINA_ZONE).toInstant();
        } catch (DateTimeParseException ex) {
            throw new IllegalArgumentException("台风预报查询时间配置格式错误", ex);
        }
    }

    /** 对上游返回的活跃台风按 ID 去重，保留每个 ID 首次出现的记录和顺序。 */
    private static List<TyphoonSummaryVo> activeStorms(List<TyphoonSummaryVo> storms) {
        Map<String, TyphoonSummaryVo> unique = new LinkedHashMap<>();
        for (TyphoonSummaryVo storm : storms) {
            unique.putIfAbsent(storm.getId(), storm);
        }
        return new ArrayList<>(unique.values());
    }

    private static Map<List<String>, Set<String>> groupByProvincePreference(List<UserProvinceVo> recipients) {
        Map<List<String>, Set<String>> groups = new LinkedHashMap<>();
        for (UserProvinceVo recipient : recipients) {
            String userId = recipient.getUserId();
            if (userId == null || userId.isBlank()) {
                continue;
            }
            List<String> selected = recipient.getProvinceCodes();
            List<String> provinces = selected == null ? new ArrayList<>() : selected.stream()
                    .filter(code -> code != null && !code.isBlank())
                    .distinct().sorted().toList();
            if (selected != null && !selected.isEmpty() && provinces.isEmpty()) {
                continue;
            }
            groups.computeIfAbsent(provinces, ignored -> new LinkedHashSet<>()).add(userId);
        }
        return groups;
    }

    private Map<String, Set<String>> buildNotificationGroups(List<TyphoonSummaryVo> storms,
            Map<List<String>, Set<String>> recipientsByPreference,
            Map<String, Map<String, TyphoonProvinceMatchDto>> matches) {
        Map<String, Set<String>> groups = new LinkedHashMap<>();
        MessageTemplateVo template = null;
        for (Map.Entry<List<String>, Set<String>> preference : recipientsByPreference.entrySet()) {
            List<String> items = new ArrayList<>();
            for (TyphoonSummaryVo storm : storms) {
                String summary = summarize(preference.getKey(), matches.get(storm.getId()));
                if (summary == null) {
                    continue;
                }
                if (template == null) {
                    MessageTemplateQueryDto query = new MessageTemplateQueryDto();
                    query.setMessageType(MESSAGE_TYPE);
                    template = Objects.requireNonNull(templateClient.get(query), "台风提醒消息模板不存在");
                }
                String name = storm.getName() == null || storm.getName().isBlank() ? storm.getId() : storm.getName();
                items.add(MessageTemplateRenderer.render(template.getItemTemplate(), name, summary));
            }
            if (items.isEmpty()) {
                continue;
            }
            String body = MessageTemplateRenderer.render(template.getBodyTemplate(), items.size(), String.join("\n", items));
            groups.computeIfAbsent(body, ignored -> new LinkedHashSet<>()).addAll(preference.getValue());
        }
        return groups;
    }

    private static String summarize(List<String> provinces, Map<String, TyphoonProvinceMatchDto> matches) {
        if (provinces.isEmpty()) {
            return "当前活跃";
        }
        if (matches == null) {
            return null;
        }
        List<String> matchedProvinces = new ArrayList<>();
        for (String province : provinces) {
            TyphoonProvinceMatchDto match = matches.get(province);
            if (match != null) {
                matchedProvinces.add(match.getProvinceName());
            }
        }
        return matchedProvinces.isEmpty() ? null : "预计涉及" + String.join("、", matchedProvinces);
    }

    private void send(String runId, Instant startedAt, String body, Collection<String> userIds) {
        NotifyEventDto event = new NotifyEventDto();
        event.setMessageType(MESSAGE_TYPE);
        event.setTitle("台风预报提醒");
        event.setBody(body);
        event.setSourceId(runId);
        event.setOccurredAt(Date.from(startedAt));
        event.setReceiverUserIds(List.copyOf(userIds));
        notifyMqProducer.send(event);
    }
}
