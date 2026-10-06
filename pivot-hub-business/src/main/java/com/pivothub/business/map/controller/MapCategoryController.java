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
 * @Description: 地图主题地点分类控制器
 * @Author lhb
 * @CreateTime 2026/10/5
 */
@RestController
@MenuPermission("map")
@RequestMapping("/business/map/category")
public class MapCategoryController {
    @Autowired
    private MapCategoryService service;

    @Operation(summary = "查询当前用户的主题地点分类")
    @GetMapping("/list")
    public Result<List<MapCategoryVo>> list(@Valid @ModelAttribute MapThemeIdDto query) {
        return Result.success(service.list(query.getThemeId()));
    }

    @Operation(summary = "新增地图地点分类")
    @PostMapping("/add")
    public Result<MapCategoryVo> add(@Valid @RequestBody MapCategoryCreateDto request) {
        return Result.success(service.add(request));
    }

    @Operation(summary = "修改地图地点分类")
    @PostMapping("/update")
    public Result<MapCategoryVo> update(@Valid @RequestBody MapCategoryUpdateDto request) {
        return Result.success(service.update(request));
    }

    @Operation(summary = "逻辑删除地图地点分类")
    @PostMapping("/delete")
    public Result<Object> delete(@Valid @RequestBody MapCategoryIdDto request) {
        service.delete(request.getCategoryId());
        return Result.success();
    }
}
