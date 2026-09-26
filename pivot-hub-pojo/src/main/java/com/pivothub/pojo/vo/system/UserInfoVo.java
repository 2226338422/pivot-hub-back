package com.pivothub.pojo.vo.system;

import lombok.Data;
import io.swagger.v3.oas.annotations.media.Schema;

@Data
@Schema(description = "当前用户资料")
public class UserInfoVo {
    @Schema(description = "用户唯一标识")
    private String uuid;
    @Schema(description = "用户编号")
    private String code;
    @Schema(description = "用户昵称")
    private String nickname;
    @Schema(description = "用户邮箱")
    private String email;
    @Schema(description = "用户头像地址")
    private String avatar;
    @Schema(description = "角色标识")
    private String roleId;
    @Schema(description = "用户状态：1-启用，0-禁用")
    private Integer status;
    @Schema(description = "备注")
    private String remark;
}
