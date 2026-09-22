package com.pivothub.pojo.po.business.community;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.pivothub.pojo.po.BasePo;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @Description: 社区互动实体（点赞/收藏）
 * @Author lhb
 * @CreateTime 2026/9/22 22:42
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class CommunityLike extends BasePo {
    @TableId(type = IdType.INPUT)
    private String uuid;

    /** 目标类型 1-帖子 2-回复 */
    private Integer targetType;

    /** 目标uuid(帖子或回复) */
    private String targetId;

    /** 操作用户uuid */
    private String userId;

    /** 互动类型 1-点赞 2-收藏 */
    private Integer actionType;

    /** 状态 0-已取消 1-生效 */
    private Integer status;
}
