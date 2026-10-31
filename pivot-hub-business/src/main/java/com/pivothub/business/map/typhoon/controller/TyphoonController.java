package com.pivothub.business.map.typhoon.controller;

import com.pivothub.business.annotation.MenuPermission;
import com.pivothub.business.map.typhoon.service.TyphoonService;
import com.pivothub.common.result.Result;
import com.pivothub.pojo.vo.business.map.typhoon.TyphoonDetailVo;
import com.pivothub.pojo.vo.business.map.typhoon.TyphoonListVo;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/** 已登录且拥有台风菜单权限的用户查询台风实况和机构预报。 */
@RestController
@MenuPermission("map_typhoon")
@RequestMapping("/business/map/typhoon")
public class TyphoonController {
    @Autowired
    private TyphoonService typhoonService;

    @Operation(summary = "查询活跃台风列表")
    @GetMapping("/list")
    public Result<TyphoonListVo> list(HttpServletResponse response) {
        response.setHeader(HttpHeaders.CACHE_CONTROL, "no-store");
        return Result.success(typhoonService.list());
    }

    @Operation(summary = "查询台风实况路径及最新预报")
    @GetMapping("/detail")
    public Result<TyphoonDetailVo> detail(@RequestParam(name = "typhoonId") String typhoonId, HttpServletResponse response) {
        response.setHeader(HttpHeaders.CACHE_CONTROL, "no-store");
        return Result.success(typhoonService.detail(typhoonId));
    }
}
