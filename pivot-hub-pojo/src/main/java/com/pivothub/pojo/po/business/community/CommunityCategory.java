package com.pivothub.pojo.po.business.community;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.pivothub.pojo.po.BasePo;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @Description: 社区板块实体（论坛分区/版块）
 * @Author lhb
 * @CreateTime 2026/9/22 22:42
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class CommunityCategory extends BasePo {
    @TableId(type = IdType.INPUT)
    private String uuid;

    /** 板块编码，唯一 */
    private String categoryCode;

    /** 板块名称 */
    private String categoryName;

    /** 父板块uuid，0为顶级 */
    private String parentId;

    /** 板块描述 */
    private String description;

    /** 板块图标 */
    private String icon;

    /** 帖子数（冗余统计） */
    private Integer postCount;

    /** 排序 */
    private Integer sort;

    /** 状态 0-禁用 1-正常 */
    private Integer status;

    /** 备注 */
    private String remark;
}
