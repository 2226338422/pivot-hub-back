package com.pivothub.pojo.po.business.map;

import lombok.Data;
import lombok.EqualsAndHashCode;
import com.pivothub.pojo.po.BasePo;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.IdType;

/**
 * @Description: 地图主题地点分类实体
 * @Author lhb
 * @CreateTime 2026/10/5
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class MapCategory extends BasePo {
    /** 分类uuid */
    @TableId(type = IdType.INPUT)
    private String uuid;

    /** 状态 0-删除 1-正常 */
    private Integer status;

    /** 分类所有者用户uuid */
    private String ownerUserId;

    /** 所属主题uuid */
    private String themeId;

    /** 分类名称 */
    private String name;

    /** 关联图标素材uuid，允许为空 */
    private String iconId;

    /** 展示排序，数值越小越靠前 */
    private Integer sort;
}
