package com.pivothub.system.notify.service.impl;

import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.pivothub.common.exception.SystemException;
import com.pivothub.common.util.TLUtil;
import com.pivothub.commoncore.enums.NotifyMessageType;
import com.pivothub.pojo.dto.system.notify.MessageSubscriptionSaveDto;
import com.pivothub.pojo.po.system.SysMenu;
import com.pivothub.pojo.po.system.SysMessageSubscription;
import com.pivothub.pojo.vo.SelectOptionVo;
import com.pivothub.pojo.vo.system.notify.MessageSubscriptionVo;
import com.pivothub.system.mapper.SysMenuMapper;
import com.pivothub.system.notify.mapper.SysMessageSubscriptionMapper;
import com.pivothub.system.notify.service.MessageSubscriptionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class MessageSubscriptionServiceImpl implements MessageSubscriptionService {
    @Autowired
    private SysMessageSubscriptionMapper sysMessageSubscriptionMapper;

    @Autowired
    private SysMenuMapper sysMenuMapper;

    @Override
    public List<SelectOptionVo> listMessageTypeOptions() {
        return sysMessageSubscriptionMapper.selectDefaultOptions();
    }

    @Override
    public List<MessageSubscriptionVo> list() {
        String userId = TLUtil.get(TLUtil.USER_ID);
        Set<String> menuIds = sysMenuMapper.selectUserMenus(userId).stream()
                .map(SysMenu::getUuid)
                .collect(Collectors.toSet());
        return sysMessageSubscriptionMapper.selectSettings(userId).stream()
                .filter(item -> menuIds.contains(item.getMenuId()))
                .toList();
    }

    @Override
    public void save(MessageSubscriptionSaveDto request) {
        String userId = TLUtil.get(TLUtil.USER_ID);
        if (!NotifyMessageType.supports(request.getMessageType())) {
            throw new SystemException(400, "不支持的消息类型");
        }
        SysMessageSubscription defaults = sysMessageSubscriptionMapper.selectDefault(request.getMessageType());
        if (defaults == null) {
            throw new SystemException(404, "未配置该消息类型");
        }
        if (sysMenuMapper.selectUserMenus(userId).stream()
                .noneMatch(menu -> defaults.getMenuId().equals(menu.getUuid()))) {
            throw new SystemException(403, "无该消息来源菜单的访问权限");
        }

        SysMessageSubscription subscription = new SysMessageSubscription();
        subscription.setUuid(IdWorker.getIdStr());
        subscription.setUserId(userId);
        subscription.setMessageType(defaults.getMessageType());
        subscription.setMenuId(defaults.getMenuId());
        subscription.setEnabled(Boolean.TRUE.equals(request.getEnabled()) ? 1 : 0);
        sysMessageSubscriptionMapper.upsertUserSetting(subscription);
    }
}
