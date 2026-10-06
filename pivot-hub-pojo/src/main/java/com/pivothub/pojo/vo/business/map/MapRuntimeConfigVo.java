package com.pivothub.pojo.vo.business.map;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "高德JS API运行配置")
public class MapRuntimeConfigVo {
    @Schema(description = "高德Web端JS API Key")
    private String jsKey;

    @Schema(description = "同源高德SDK代理入口地址")
    private String serviceHost;
}
