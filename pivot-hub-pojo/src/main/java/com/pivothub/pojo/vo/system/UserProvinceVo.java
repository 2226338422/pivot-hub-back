package com.pivothub.pojo.vo.system;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

/** 用户 UUID 和所选省份。 */
@Data
@Schema(title = "用户所在省份")
public class UserProvinceVo {
    @Schema(description = "用户 UUID")
    private String userId;

    @Schema(description = "所在省份编码列表")
    private List<String> provinceCodes;
}
