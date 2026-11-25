package com.pivothub.system.controller;

import com.pivothub.common.result.Result;
import com.pivothub.commoncore.constants.system.ScheduledTaskConstants;
import com.pivothub.pojo.dto.PageDto;
import com.pivothub.pojo.dto.system.ScheduledTaskPageDto;
import com.pivothub.pojo.vo.PageVo;
import com.pivothub.pojo.vo.system.ScheduledTaskRunVo;
import com.pivothub.pojo.vo.system.ScheduledTaskVo;
import com.pivothub.system.annotation.MenuPermission;
import com.pivothub.system.service.ScheduledTaskService;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/system/scheduled-tasks")
@MenuPermission(ScheduledTaskConstants.MENU_CODE)
public class SystemScheduledTaskController {
    @Autowired
    private ScheduledTaskService scheduledTaskService;

    @Operation(summary = "手动执行定时任务")
    @PostMapping("/run")
    public Result<Integer> run(@RequestParam("taskCode") String taskCode) {
        return Result.success(scheduledTaskService.runNow(taskCode));
    }

    @Operation(summary = "分页查询定时任务")
    @GetMapping("/page")
    public Result<PageVo<ScheduledTaskVo>> page(ScheduledTaskPageDto query) {
        return Result.success(scheduledTaskService.page(query));
    }

    @Operation(summary = "分页查询定时任务执行记录")
    @GetMapping("/{taskCode}/runs")
    public Result<PageVo<ScheduledTaskRunVo>> pageRuns(@PathVariable("taskCode") String taskCode, PageDto query) {
        return Result.success(scheduledTaskService.pageRuns(taskCode, query));
    }

    @Operation(summary = "查询定时任务执行记录详情")
    @GetMapping("/runs/{runId}")
    public Result<ScheduledTaskRunVo> runDetail(@PathVariable("runId") String runId) {
        return Result.success(scheduledTaskService.runDetail(runId));
    }
}
