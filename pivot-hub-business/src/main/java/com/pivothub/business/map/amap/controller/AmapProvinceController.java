package com.pivothub.business.map.amap.controller;

import com.pivothub.business.map.amap.service.AmapService;
import com.pivothub.common.result.Result;
import com.pivothub.common.util.TLUtil;
import com.pivothub.pojo.vo.SelectOptionVo;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/business/map/amap")
public class AmapProvinceController {
    @Autowired
    private AmapService amapService;

    @Operation(summary = "查询省级行政区选项")
    @GetMapping("/province-options")
    public Result<List<SelectOptionVo>> provinceOptions() {
        return Result.success(amapService.provinceOptions(TLUtil.get(TLUtil.USER_ID)));
    }
}
