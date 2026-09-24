package com.pivothub.pojo.po.business.community;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.pivothub.pojo.po.BasePo;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @Description: 社区帖子点赞实体
 * @Author lhb
 * @CreateTime 2026/9/25
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class CommunityPostLike extends BasePo {
    @TableId(type = IdType.INPUT)
    private String uuid;

    /** 帖子uuid */
    private String postId;

    /** 点赞用户uuid */
    private String userId;

    /** 状态 0-取消点赞 1-已点赞 */
    private Integer status;
}
