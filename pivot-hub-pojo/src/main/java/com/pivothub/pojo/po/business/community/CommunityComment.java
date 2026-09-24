package com.pivothub.pojo.po.business.community;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.pivothub.pojo.po.BasePo;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @Description: 社区评论实体
 * @Author lhb
 * @CreateTime 2026/9/25
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class CommunityComment extends BasePo {
    @TableId(type = IdType.INPUT)
    private String uuid;

    /** 所属帖子uuid */
    private String postId;

    /** 父评论uuid，0为顶级评论 */
    private String parentId;

    /** 根评论uuid，用于楼中楼查询 */
    private String rootId;

    /** 评论用户uuid */
    private String authorId;

    /** 被回复用户uuid */
    private String replyToUserId;

    /** 评论内容 */
    private String content;

    /** 点赞数 */
    private Integer likeCount;

    /** 状态 0-删除 1-正常 */
    private Integer status;

    /** 备注 */
    private String remark;
}
