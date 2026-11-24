package com.pivothub.system.notify.mapper;

import com.pivothub.pojo.po.system.SysMessageSubscription;
import com.pivothub.pojo.vo.SelectOptionVo;
import com.pivothub.pojo.vo.system.notify.MessageSubscriptionVo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/** 消息订阅默认配置和用户设置。 */
@Mapper
public interface SysMessageSubscriptionMapper {
    /** 查询全局默认订阅配置中的消息类型选项。 */
    List<SelectOptionVo> selectDefaultOptions();

    /** 查询默认展示配置及当前用户的有效开关。 */
    List<MessageSubscriptionVo> selectSettings(@Param("userId") String userId);

    /** 根据类型查询全局默认配置。 */
    SysMessageSubscription selectDefault(@Param("messageType") String messageType);

    /** 新增或更新用户对指定类型的设置。 */
    int upsertUserSetting(SysMessageSubscription subscription);

    /** 查询已订阅的活跃用户 UUID，可限定候选名单。 */
    List<String> selectSubscribedUserIds(@Param("messageType") String messageType,
                                         @Param("defaultEnabled") Integer defaultEnabled,
                                         @Param("candidateUserIds") List<String> candidateUserIds);
}
