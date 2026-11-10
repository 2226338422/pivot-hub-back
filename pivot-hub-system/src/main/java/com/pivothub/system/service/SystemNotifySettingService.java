package com.pivothub.system.service;

import com.pivothub.pojo.dto.system.notify.NotifySettingDto;
import com.pivothub.pojo.vo.system.notify.NotifySettingsVo;

/** 用户通知设置。 */
public interface SystemNotifySettingService {

    /** 查询当前用户的渠道设置，用户行覆盖默认行；需绑定但未绑定的渠道回退到默认值。 */
    NotifySettingsVo getSettings();

    /** 保存渠道开关，未满足开启条件的渠道会被拒绝。 */
    void updateSettings(NotifySettingDto dto);
}
