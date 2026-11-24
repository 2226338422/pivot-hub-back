package com.pivothub.pojo.dto.system;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

import java.util.List;

@Data
@Schema(description = "批量保存系统设置请求")
public class SystemConfigSaveDto {
    @Schema(description = "待保存的键值配置列表")
    @Valid
    @NotEmpty(message = "至少提交一项配置")
    private List<SystemConfigItemDto> items;
}
