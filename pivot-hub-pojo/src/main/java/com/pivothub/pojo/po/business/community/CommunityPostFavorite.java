package com.pivothub.pojo.po.business.community;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.pivothub.pojo.po.BasePo;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @Description: 社区帖子收藏实体
 * @Author lhb
 * @CreateTime 2026/9/25
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class CommunityPostFavorite extends BasePo {
    @TableId(type = IdType.INPUT)
    private String uuid;

    /** 帖子uuid */
    private String postId;

    /** 收藏用户uuid */
    private String userId;

    /** 状态 0-取消收藏 1-已收藏 */
    private Integer status;
}
