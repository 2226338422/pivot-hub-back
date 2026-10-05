package com.pivothub.pojo.dto.system;

import com.pivothub.pojo.dto.PageDto;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@Schema(description = "角色分页查询")
public class RolePageDto extends PageDto {
    @Schema(description = "角色名称，包含匹配")
    private String name;
    @Schema(description = "角色编码，包含匹配")
    private String code;
}
