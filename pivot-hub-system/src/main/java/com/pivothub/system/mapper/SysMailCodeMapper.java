package com.pivothub.system.mapper;

import com.pivothub.pojo.po.system.SysMailCode;
import org.apache.ibatis.annotations.Param;

import java.util.Date;

public interface SysMailCodeMapper {
    SysMailCode selectUsable(@Param("email") String email,
                             @Param("scene") String scene,
                             @Param("now") Date now);

    int markUsed(@Param("uuid") String uuid);

    int insert(SysMailCode code);
}
