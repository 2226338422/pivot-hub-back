package com.pivothub.system.mapper;

import com.pivothub.pojo.dto.system.ScheduledTaskPageDto;
import com.pivothub.pojo.po.system.SysScheduledTask;
import com.pivothub.pojo.vo.system.ScheduledTaskDefinitionVo;
import com.pivothub.pojo.vo.system.ScheduledTaskVo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface SysScheduledTaskMapper {
    List<ScheduledTaskVo> selectPage(ScheduledTaskPageDto query);

    SysScheduledTask selectByCode(@Param("taskCode") String taskCode);

    SysScheduledTask selectActiveByCode(@Param("taskCode") String taskCode);

    List<ScheduledTaskDefinitionVo> selectEnabledByService(@Param("serviceName") String serviceName);
}
