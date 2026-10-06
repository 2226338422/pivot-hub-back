package com.pivothub.pojo.vo.business.map;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import java.util.Date;

@Data
@Schema(description = "地图地点分类信息")
public class MapCategoryVo {
    @Schema(description = "分类唯一标识")
    private String uuid;

    @Schema(description = "所属主题唯一标识")
    private String themeId;

    @Schema(description = "分类名称")
    private String name;

    @Schema(description = "关联图标素材标识，未绑定时为空")
    private String iconId;

    @Schema(description = "分类展示排序")
    private Integer sort;

    @Schema(description = "创建时间")
    private Date createTime;

    @Schema(description = "最后更新时间")
    private Date updateTime;
}
