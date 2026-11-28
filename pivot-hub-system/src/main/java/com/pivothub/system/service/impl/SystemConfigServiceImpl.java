package com.pivothub.system.service.impl;

import com.pivothub.common.exception.SystemException;
import com.pivothub.common.util.TLUtil;
import com.pivothub.commoncore.enums.SystemConfigType;
import com.pivothub.pojo.dto.system.SystemConfigItemDto;
import com.pivothub.pojo.vo.system.SystemConfigVo;
import com.pivothub.system.mapper.SysConfigMapper;
import com.pivothub.system.service.SystemConfigService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.format.ResolverStyle;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class SystemConfigServiceImpl implements SystemConfigService {
    private static final DateTimeFormatter DATETIME_FORMAT = DateTimeFormatter
            .ofPattern("uuuu-MM-dd HH:mm:ss").withResolverStyle(ResolverStyle.STRICT);

    @Autowired
    private SysConfigMapper configMapper;

    @Override
    public List<SystemConfigVo> listAuthorizedConfigs() {
        return configMapper.selectAuthorizedConfigs(TLUtil.get(TLUtil.USER_ID));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void saveConfigs(List<SystemConfigItemDto> items) {
        String userId = TLUtil.get(TLUtil.USER_ID);
        Map<String, SystemConfigVo> configs = configMapper.selectAuthorizedConfigs(userId).stream()
                .collect(Collectors.toMap(SystemConfigVo::getConfigKey, Function.identity()));
        Map<String, SystemConfigVo> updates = new LinkedHashMap<>();
        for (SystemConfigItemDto item : items) {
            if (item == null) {
                continue;
            }
            String key = item.getKey();
            String value = item.getValue();
            SystemConfigVo config = configs.get(key);
            SystemConfigType type = config == null ? null : SystemConfigType.findByValue(config.getConfigType());
            if (type == null) {
                throw new SystemException(403, "无权修改系统配置：" + key);
            }
            config.setConfigValue(normalizeConfigValue(type, value));
            updates.put(key, config);
        }
        List<SystemConfigVo> updateList = new ArrayList<>(updates.values());
        if (!updateList.isEmpty()
                && configMapper.batchUpdateAuthorizedConfigs(updateList, userId) != updateList.size()) {
            throw new SystemException(403, "无权修改部分系统配置");
        }
    }

    private String normalizeConfigValue(SystemConfigType type, String value) {
        if (type == SystemConfigType.SINGLE_SELECT || type == SystemConfigType.MULTI_SELECT) {
            throw new SystemException(400, "该配置暂无可用选项");
        }
        String trimmed = value.trim();
        if (type == SystemConfigType.DATETIME) {
            if (trimmed.isEmpty()) {
                return "";
            }
            try {
                LocalDateTime.parse(trimmed, DATETIME_FORMAT);
                return trimmed;
            } catch (DateTimeParseException ex) {
                throw new SystemException(400, "日期时间配置值须符合 yyyy-MM-dd HH:mm:ss 格式");
            }
        }
        try {
            return switch (type) {
                case SWITCH -> {
                    if (!"true".equals(trimmed) && !"false".equals(trimmed)) {
                        throw new SystemException(400, "开关配置值须为 true 或 false");
                    }
                    yield trimmed;
                }
                case INTEGER -> Integer.toString(Integer.parseInt(trimmed));
                case DECIMAL -> new BigDecimal(trimmed).toString();
                default -> value;
            };
        } catch (NumberFormatException ex) {
            throw new SystemException(400, "配置值须为有效的" + type.getName());
        }
    }

    @Override
    public String getConfigValue(String key) {
        return configMapper.selectActiveConfigValue(key);
    }
}
