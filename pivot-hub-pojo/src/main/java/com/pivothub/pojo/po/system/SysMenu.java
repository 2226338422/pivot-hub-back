package com.pivothub.pojo.po.system;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.pivothub.pojo.po.BasePo;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 菜单实体
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class SysMenu extends BasePo {
    @TableId(type = IdType.INPUT)
    private String uuid;

    private String code;

    private String menuName;

    /** 父菜单uuid，0为顶级 */
    private String parentId;

    /** 模块uuid */
    private String moduleId;

    /** Web端路由路径 */
    private String webUrl;

    /** App端路由路径 */
    private String appUrl;

    /** 图标 */
    private String icon;

    /** 排序 */
    private Integer sort;

    /** 状态 0-禁用 1-正常 */
    private Integer status;
}
