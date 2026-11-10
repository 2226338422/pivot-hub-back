package com.pivothub.system.service.impl;

import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.pivothub.common.exception.BusinessException;
import com.pivothub.common.util.TLUtil;
import com.pivothub.commoncore.enums.NotifyChannel;
import com.pivothub.pojo.dto.system.notify.NotifyChannelSettingDto;
import com.pivothub.pojo.dto.system.notify.NotifySettingDto;
import com.pivothub.pojo.po.system.SysNotifySetting;
import com.pivothub.pojo.po.system.SysUser;
import com.pivothub.pojo.vo.system.notify.NotifyChannelSettingVo;
import com.pivothub.pojo.vo.system.notify.NotifySettingsVo;
import com.pivothub.system.mapper.SystemNotifySettingMapper;
import com.pivothub.system.mapper.SysUserMapper;
import com.pivothub.system.notify.strategy.NotifyChannelStrategy;
import com.pivothub.system.notify.strategy.NotifyChannelStrategyFactory;
import com.pivothub.system.service.SystemNotifySettingService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/** 用户通知设置实现，默认行 + 用户行合并，需绑定的渠道经策略工厂校验。 */
@Service
public class SystemNotifySettingServiceImpl implements SystemNotifySettingService {
    private static final Logger LOG = LoggerFactory.getLogger(SystemNotifySettingServiceImpl.class);

    @Autowired
    private SystemNotifySettingMapper notifySettingMapper;

    @Autowired
    private SysUserMapper userMapper;

    @Autowired
    private NotifyChannelStrategyFactory strategyFactory;

    @Override
    public NotifySettingsVo getSettings() {
        String userId = currentUserId();
        SysUser user = userMapper.selectById(userId);
        List<SysNotifySetting> defaults = notifySettingMapper.selectDefaults();
        Map<String, SysNotifySetting> userRows = toMap(notifySettingMapper.selectByUserId(userId));

        List<NotifyChannelSettingVo> channels = new ArrayList<>();
        // 以默认行（已按 sort 正序）为基准遍历，保证「没有用户行」的渠道也会返回
        for (SysNotifySetting defaultRow : defaults) {
            String channelValue = defaultRow.getChannel();
            SysNotifySetting userRow = userRows.get(channelValue);
            channels.add(buildChannel(defaultRow, userRow, user, userId));
        }

        NotifySettingsVo vo = new NotifySettingsVo();
        vo.setChannels(channels);
        return vo;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateSettings(NotifySettingDto dto) {
        String userId = currentUserId();
        SysUser user = userMapper.selectById(userId);
        Map<String, SysNotifySetting> defaults = toMap(notifySettingMapper.selectDefaults());
        Map<String, SysNotifySetting> userRows = toMap(notifySettingMapper.selectByUserId(userId));

        // 同一渠道重复提交以最后一次为准
        Map<String, Boolean> changes = new LinkedHashMap<>();
        for (NotifyChannelSettingDto item : dto.getChannels()) {
            NotifyChannel channel = parseChannel(item.getChannel());
            changes.put(channel.getValue(), Boolean.TRUE.equals(item.getEnabled()));
        }

        for (Map.Entry<String, Boolean> entry : changes.entrySet()) {
            String channelValue = entry.getKey();
            boolean enable = entry.getValue();
            SysNotifySetting defaultRow = defaults.get(channelValue);

            if (enable) {
                ensureAvailable(channelValue, defaultRow, user, userId);
            }
            Integer enabled = enable ? 1 : 0;
            if (userRows.containsKey(channelValue)) {
                notifySettingMapper.updateEnabled(userId, channelValue, enabled);
            } else {
                insertUserRow(userId, channelValue, enabled, defaultRow);
            }
        }
        LOG.info("通知设置已保存 userId={} changes={}", userId, changes);
    }

    /** 构建单个渠道：名称与说明文案来自表数据，可用性由策略判断。 */
    private NotifyChannelSettingVo buildChannel(SysNotifySetting defaultRow, SysNotifySetting userRow,
                                               SysUser user, String userId) {
        String channelValue = defaultRow.getChannel();
        NotifyChannelStrategy strategy = strategyFactory.get(channelValue);

        NotifyChannelSettingVo item = new NotifyChannelSettingVo();
        item.setChannel(channelValue);
        item.setName(defaultRow.getName());
        item.setDetail(defaultRow.getDetail());
        item.setRequireBind(Integer.valueOf(1).equals(defaultRow.getRequireBind()));

        boolean available = strategy != null && strategy.isAvailable(user, userId);
        item.setAvailable(available);

        // 开关取值：用户行存在且已可用 → 用户行值；否则默认行值
        if (userRow != null && available) {
            item.setEnabled(Integer.valueOf(1).equals(userRow.getEnabled()));
        } else {
            item.setEnabled(Integer.valueOf(1).equals(defaultRow.getEnabled()));
        }
        return item;
    }

    /** 开启前校验渠道条件，未绑定不允许开启。 */
    private void ensureAvailable(String channelValue, SysNotifySetting defaultRow, SysUser user, String userId) {
        if (defaultRow == null) {
            throw new BusinessException(400, "未知的通知渠道：" + channelValue);
        }
        NotifyChannelStrategy strategy = strategyFactory.get(channelValue);
        if (strategy == null || !strategy.isAvailable(user, userId)) {
            throw new BusinessException(400, "请先绑定" + NotifyChannel.fromValue(channelValue).getName() + "通知");
        }
    }

    private void insertUserRow(String userId, String channel, Integer enabled, SysNotifySetting defaultRow) {
        SysNotifySetting record = new SysNotifySetting();
        record.setUuid(IdWorker.getIdStr());
        record.setUserId(userId);
        record.setChannel(channel);
        record.setName(defaultRow == null ? null : defaultRow.getName());
        record.setDetail(defaultRow == null ? null : defaultRow.getDetail());
        record.setEnabled(enabled);
        record.setRequireBind(defaultRow == null ? 0 : defaultRow.getRequireBind());
        record.setIsDefault(0);
        record.setSort(defaultRow == null ? 0 : defaultRow.getSort());
        record.setCreateUserId(userId);
        record.setUpdateUserId(userId);
        notifySettingMapper.insert(record);
    }

    private NotifyChannel parseChannel(String value) {
        try {
            return NotifyChannel.fromValue(value);
        } catch (IllegalArgumentException ex) {
            throw new BusinessException(400, ex.getMessage());
        }
    }

    private String currentUserId() {
        String userId = TLUtil.get(TLUtil.USER_ID);
        if (!StringUtils.hasText(userId)) {
            throw new BusinessException(500, "用户身份上下文不可用");
        }
        return userId;
    }

    private Map<String, SysNotifySetting> toMap(List<SysNotifySetting> rows) {
        return rows.stream().collect(Collectors.toMap(
                SysNotifySetting::getChannel,
                Function.identity(),
                (left, right) -> right));
    }
}
