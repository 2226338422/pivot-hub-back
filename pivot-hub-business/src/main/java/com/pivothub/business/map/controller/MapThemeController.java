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
 * @Description: 私有地图主题控制器
 * @Author lhb
 * @CreateTime 2026/10/5
 */
@RestController
@MenuPermission("map")
@RequestMapping("/business/map/theme")
public class MapThemeController {
    @Autowired
    private MapThemeService service;

    @Operation(summary = "查询当前用户的地图主题")
    @GetMapping("/list")
    public Result<List<MapThemeVo>> list() {
        return Result.success(service.list());
    }

    @Operation(summary = "获取地图主题详情")
    @GetMapping("/detail")
    public Result<MapThemeVo> detail(@Valid @ModelAttribute MapThemeIdDto query) {
        return Result.success(service.detail(query.getThemeId()));
    }

    @Operation(summary = "新增私有地图主题")
    @PostMapping("/add")
    public Result<MapThemeVo> add(@Valid @RequestBody MapThemeCreateDto request) {
        return Result.success(service.add(request));
    }

    @Operation(summary = "修改私有地图主题")
    @PostMapping("/update")
    public Result<MapThemeVo> update(@Valid @RequestBody MapThemeUpdateDto request) {
        return Result.success(service.update(request));
    }

    @Operation(summary = "逻辑删除地图主题及其分类和地点")
    @PostMapping("/delete")
    public Result<Object> delete(@Valid @RequestBody MapThemeIdDto request) {
        service.delete(request.getThemeId());
        return Result.success();
    }
}
