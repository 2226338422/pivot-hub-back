package com.pivothub.pojo.po.business.community;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.pivothub.pojo.po.BasePo;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Date;

/**
 * @Description: 社区帖子实体（论坛主题帖）
 * @Author lhb
 * @CreateTime 2026/9/22 22:42
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class CommunityPost extends BasePo {
    @TableId(type = IdType.INPUT)
    private String uuid;

    /** 所属板块uuid */
    private String categoryId;

    /** 帖子标题 */
    private String title;

    /** 帖子正文内容 */
    private String content;

    /** 作者用户uuid */
    private String authorId;

    /** 作者昵称（冗余，便于列表展示） */
    private String authorName;

    /** 帖子类型 1-普通 2-求助 3-分享 */
    private Integer postType;

    /** 是否置顶 0-否 1-是 */
    private Integer topFlag;

    /** 是否精华 0-否 1-是 */
    private Integer digestFlag;

    /** 浏览数 */
    private Integer viewCount;

    /** 回复数 */
    private Integer replyCount;

    /** 点赞数 */
    private Integer likeCount;

    /** 收藏数 */
    private Integer collectCount;

    /** 最后回复时间（用于列表排序） */
    private Date lastReplyTime;

    /** 最后回复人uuid */
    private String lastReplyUserId;

    /** 状态 0-下架 1-正常 */
    private Integer status;

    /** 备注 */
    private String remark;
}
