package com.pivothub.pojo.po.system;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.pivothub.pojo.po.BasePo;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @Description: 菜单实体
 * @Author lhb
 * @CreateTime 2026/9/22 22:42
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class SysMenu extends BasePo {
    @TableId(type = IdType.INPUT)
    private String uuid;

    /** 菜单编码 */
    private String code;

    /** 菜单名称 */
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
