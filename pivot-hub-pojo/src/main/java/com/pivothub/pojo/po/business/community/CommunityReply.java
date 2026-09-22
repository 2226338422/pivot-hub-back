package com.pivothub.pojo.po.business.community;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.pivothub.pojo.po.BasePo;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @Description: 社区回帖实体（楼层/楼中楼回复）
 * @Author lhb
 * @CreateTime 2026/9/22 22:42
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class CommunityReply extends BasePo {
    @TableId(type = IdType.INPUT)
    private String uuid;

    /** 所属帖子uuid */
    private String postId;

    /** 父回复uuid，0为顶级(主楼回复) */
    private String parentId;

    /** 根回复uuid，用于楼中楼归属主楼 */
    private String rootId;

    /** 回复内容 */
    private String content;

    /** 作者用户uuid */
    private String authorId;

    /** 作者昵称（冗余，便于列表展示） */
    private String authorName;

    /** 楼层号 */
    private Integer floorNum;

    /** 回复数（针对主楼） */
    private Integer replyCount;

    /** 点赞数 */
    private Integer likeCount;

    /** 状态 0-删除 1-正常 */
    private Integer status;

    /** 备注 */
    private String remark;
}
