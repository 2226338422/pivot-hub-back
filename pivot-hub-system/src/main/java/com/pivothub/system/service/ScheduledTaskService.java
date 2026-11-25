package com.pivothub.system.service;

import com.pivothub.pojo.dto.PageDto;
import com.pivothub.pojo.dto.system.ScheduledTaskPageDto;
import com.pivothub.pojo.dto.system.feign.ScheduledTaskRunFinishDto;
import com.pivothub.pojo.dto.system.feign.ScheduledTaskRunStartDto;
import com.pivothub.pojo.vo.PageVo;
import com.pivothub.pojo.vo.system.ScheduledTaskDefinitionVo;
import com.pivothub.pojo.vo.system.ScheduledTaskRunVo;
import com.pivothub.pojo.vo.system.ScheduledTaskVo;

import java.util.List;

public interface ScheduledTaskService {
    int runNow(String taskCode);

    PageVo<ScheduledTaskVo> page(ScheduledTaskPageDto query);

    PageVo<ScheduledTaskRunVo> pageRuns(String taskCode, PageDto query);

    ScheduledTaskRunVo runDetail(String runId);

    List<ScheduledTaskDefinitionVo> listEnabled(String serviceName);

    String startRun(ScheduledTaskRunStartDto dto);

    void finishRun(String runId, ScheduledTaskRunFinishDto dto);
}
