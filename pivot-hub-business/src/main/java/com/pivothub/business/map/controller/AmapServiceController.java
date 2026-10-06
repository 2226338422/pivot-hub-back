package com.pivothub.business.map.controller;

import io.swagger.v3.oas.annotations.Operation;
import org.springframework.beans.factory.annotation.Autowired;

import com.pivothub.business.annotation.MenuPermission;

import com.pivothub.business.map.service.AmapService;
import com.pivothub.common.result.Result;
import com.pivothub.common.util.TLUtil;
import com.pivothub.pojo.dto.business.map.AmapCoordinateDto;
import com.pivothub.pojo.dto.business.map.AmapPoiIdDto;
import com.pivothub.pojo.dto.business.map.AmapPoiPageDto;
import com.pivothub.pojo.dto.business.map.AmapRouteRequestDto;
import com.pivothub.pojo.dto.business.map.AmapSearchRequestDto;
import com.pivothub.pojo.vo.business.map.MapCityOptionVo;
import com.pivothub.pojo.vo.business.map.MapCityVo;
import com.pivothub.pojo.vo.business.map.MapRouteVo;
import com.pivothub.pojo.vo.business.map.MapReverseGeocodeVo;
import com.pivothub.pojo.vo.PageVo;
import com.pivothub.pojo.vo.business.map.SearchPoiVo;
import com.pivothub.pojo.vo.business.map.SearchTipVo;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpHeaders;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;

/**
 * @Description: 高德地图查询与路线服务控制器
 * @Author lhb
 * @CreateTime 2026/10/5
 */
@RestController
@MenuPermission("map")
@RequestMapping("/business/map/amap")
public class AmapServiceController {
    @Autowired
    private AmapService service;
    @Operation(summary = "识别当前访问网络所在城市")
    @GetMapping("/city")
    public Result<MapCityVo> city(HttpServletRequest request, HttpServletResponse response) {
        response.setHeader(HttpHeaders.CACHE_CONTROL, "no-store");
        return Result.success(service.city(request, TLUtil.USER_ID.get()));
    }

    @Operation(summary = "获取地图搜索城市选项")
    @GetMapping("/cities")
    public Result<List<MapCityOptionVo>> cities(HttpServletResponse response) {
        response.setHeader(HttpHeaders.CACHE_CONTROL, "no-store");
        return Result.success(service.cities(TLUtil.USER_ID.get()));
    }

    @Operation(summary = "查询地址输入提示")
    @GetMapping("/searchTips")
    public Result<List<SearchTipVo>> searchTips(AmapSearchRequestDto request) {
        return Result.success(service.searchTips(request, TLUtil.USER_ID.get()));
    }

    @Operation(summary = "分页搜索高德地点")
    @GetMapping("/searchPois")
    public Result<PageVo<SearchPoiVo>> searchPois(AmapPoiPageDto request) {
        return Result.success(service.searchPois(request, TLUtil.USER_ID.get()));
    }

    @Operation(summary = "获取高德地点详情")
    @GetMapping("/poiDetail")
    public Result<SearchPoiVo> poiDetail(AmapPoiIdDto request) {
        return Result.success(service.poiDetail(request, TLUtil.USER_ID.get()));
    }

    @Operation(summary = "根据地图坐标查询地址及附近地点")
    @GetMapping("/reverseGeocode")
    public Result<MapReverseGeocodeVo> reverseGeocode(AmapCoordinateDto request) {
        return Result.success(service.reverseGeocode(request, TLUtil.USER_ID.get()));
    }

    @Operation(summary = "规划高德出行路线")
    @PostMapping("/route")
    public Result<MapRouteVo> route(@RequestBody AmapRouteRequestDto request) {
        return Result.success(service.route(request, TLUtil.USER_ID.get()));
    }
}
