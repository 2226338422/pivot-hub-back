package com.pivothub.system.mapper;

import com.pivothub.pojo.po.system.SysUserPlatform;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface SysUserPlatformMapper {
    SysUserPlatform selectByUserId(@Param("userId") String userId);

    /** 查询指定 WxPusher uid 当前生效的绑定（wxpusher_status = 1）。 */
    SysUserPlatform selectActiveByWxpusherUid(@Param("uid") String uid);

    int insert(SysUserPlatform record);

    int updateWxpusher(SysUserPlatform record);

    /** 将指定 uid 下的生效绑定置为未绑定，保留 uid 作为历史记录。 */
    int deactivateByWxpusherUid(@Param("uid") String uid);

    /** 解除当前用户的绑定：只置未绑定，保留 uid。 */
    int unbind(@Param("userId") String userId);
}
