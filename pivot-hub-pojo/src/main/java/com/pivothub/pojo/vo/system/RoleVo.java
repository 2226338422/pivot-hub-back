package com.pivothub.pojo.vo.system;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import java.util.Date;

@Data
@Schema(description = "角色详情")
public class RoleVo {
    @Schema(description = "角色唯一标识")
    private String uuid;
    @Schema(description = "角色编码")
    private String code;
    @Schema(description = "角色名称")
    private String name;
    @Schema(description = "是否默认角色")
    private Integer defa;
    @Schema(description = "角色排序")
    private Integer sort;
    @Schema(description = "角色状态 0-已删除 1-正常")
    private Integer status;
    @Schema(description = "备注")
    private String remark;
    @Schema(description = "创建时间")
    private Date createTime;
    @Schema(description = "更新时间")
    private Date updateTime;
}
