package com.pivothub.pojo.po.business.community;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.pivothub.pojo.po.BasePo;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @Description: 社区帖子实体
 * @Author lhb
 * @CreateTime 2026/9/25
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class CommunityPost extends BasePo {
    @TableId(type = IdType.INPUT)
    private String uuid;

    /** 发布用户uuid */
    private String authorId;

    /** 帖子标题 */
    private String title;

    /** 帖子正文 */
    private String content;

    /** 帖子类型，预留扩展 */
    private Integer postType;

    /** 浏览数 */
    private Integer viewCount;

    /** 评论数 */
    private Integer commentCount;

    /** 点赞数 */
    private Integer likeCount;

    /** 收藏数 */
    private Integer favoriteCount;

    /** 状态 0-删除/隐藏 1-正常 */
    private Integer status;

    /** 备注 */
    private String remark;
}
