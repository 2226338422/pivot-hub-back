package com.pivothub.system.mapper;

import com.pivothub.pojo.po.system.SysNotifySetting;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface SystemNotifySettingMapper {
    /** 查询全局默认行（user_id IS NULL）。 */
    List<SysNotifySetting> selectDefaults();

    /** 查询指定用户的设置行。 */
    List<SysNotifySetting> selectByUserId(@Param("userId") String userId);

    int insert(SysNotifySetting record);

    int updateEnabled(@Param("userId") String userId,
                      @Param("channel") String channel,
                      @Param("enabled") Integer enabled);
}
