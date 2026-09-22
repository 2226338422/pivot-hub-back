package com.pivothub.pojo.po.business.community;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.pivothub.pojo.po.BasePo;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @Description: 社区关注关系实体（粉丝/关注）
 * @Author lhb
 * @CreateTime 2026/9/22 22:42
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class CommunityFollow extends BasePo {
    @TableId(type = IdType.INPUT)
    private String uuid;

    /** 关注人用户uuid(粉丝) */
    private String followerId;

    /** 被关注人用户uuid */
    private String followeeId;

    /** 状态 0-已取消 1-已关注 */
    private Integer status;

    /** 备注 */
    private String remark;
}
