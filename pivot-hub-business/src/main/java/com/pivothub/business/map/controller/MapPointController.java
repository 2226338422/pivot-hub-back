package com.pivothub.business.map.controller;

import io.swagger.v3.oas.annotations.Operation;
import org.springframework.beans.factory.annotation.Autowired;

import com.pivothub.business.annotation.MenuPermission;

import com.pivothub.business.map.service.*;
import com.pivothub.common.result.Result;
import com.pivothub.pojo.dto.business.map.*;
import com.pivothub.pojo.vo.business.map.*;
import com.pivothub.pojo.vo.PageVo;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.util.List;

/**
 * @Description: 私有地图地点与笔记控制器
 * @Author lhb
 * @CreateTime 2026/10/5
 */
@RestController
@MenuPermission("map")
@RequestMapping("/business/map/point")
public class MapPointController {
    @Autowired
    private MapPointService service;

    @Operation(summary = "分页查询当前用户的已保存地点")
    @GetMapping("/page")
    public Result<PageVo<MapPointSummaryVo>> page(@Valid @ModelAttribute MapPointPageDto query) {
        return Result.success(service.page(query));
    }

    @Operation(summary = "查询当前主题的地图地点标记")
    @GetMapping("/markers")
    public Result<List<MapPointSummaryVo>> markers(@Valid @ModelAttribute MapPointQueryDto query) {
        return Result.success(service.markers(query));
    }

    @Operation(summary = "获取地图地点完整详情")
    @GetMapping("/detail")
    public Result<MapPointVo> detail(@Valid @ModelAttribute MapPointIdDto query) {
        return Result.success(service.detail(query.getPointId()));
    }

    @Operation(summary = "新增地图地点")
    @PostMapping("/add")
    public Result<MapPointVo> add(@Valid @RequestBody MapPointCreateDto request) {
        return Result.success(service.add(request));
    }

    @Operation(summary = "修改地图地点及笔记")
    @PostMapping("/update")
    public Result<MapPointVo> update(@Valid @RequestBody MapPointUpdateDto request) {
        return Result.success(service.update(request));
    }

    @Operation(summary = "逻辑删除地图地点")
    @PostMapping("/delete")
    public Result<Object> delete(@Valid @RequestBody MapPointIdDto request) {
        service.delete(request.getPointId());
        return Result.success();
    }
}
