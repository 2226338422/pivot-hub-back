package com.pivothub.system.mapper;

import com.pivothub.pojo.po.system.SysScheduledTaskRun;
import com.pivothub.pojo.vo.system.ScheduledTaskRunVo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface SysScheduledTaskRunMapper {
    List<ScheduledTaskRunVo> selectPageByTaskId(@Param("taskId") String taskId);

    ScheduledTaskRunVo selectById(@Param("runId") String runId);

    int insert(SysScheduledTaskRun run);

    int finish(SysScheduledTaskRun run);
}
