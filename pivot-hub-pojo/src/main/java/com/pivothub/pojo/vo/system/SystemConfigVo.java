package com.pivothub.pojo.vo.system;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(title = "系统设置")
public class SystemConfigVo {
    @Schema(description = "配置 UUID")
    private String uuid;
    @Schema(description = "配置键")
    private String configKey;
    @Schema(description = "配置名称")
    private String configName;
    @Schema(description = "配置类型：1-文本，2-开关，3-整数，4-小数，5-单选，6-多选")
    private Integer configType;
    @Schema(description = "配置值，按文本返回")
    private String configValue;
    @Schema(description = "配置备注")
    private String remark;
}
