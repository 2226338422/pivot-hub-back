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
    @Schema(description = "头像文件uuid（关联 sys_file.uuid，无头像时为默认头像uuid）")
    private String avatarFileId;
    @Schema(description = "角色标识")
    private String roleId;
    @Schema(description = "用户状态：1-启用，0-禁用")
    private Integer status;
    @Schema(description = "备注")
    private String remark;
}
