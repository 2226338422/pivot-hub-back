package com.pivothub.system.mapper;

import com.pivothub.pojo.vo.system.SystemConfigVo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface SysConfigMapper {
    List<SystemConfigVo> selectAuthorizedConfigs(@Param("userId") String userId);

    int batchUpdateAuthorizedConfigs(@Param("configs") List<SystemConfigVo> configs,
                                     @Param("userId") String userId);

    String selectActiveConfigValue(@Param("configKey") String configKey);
}
