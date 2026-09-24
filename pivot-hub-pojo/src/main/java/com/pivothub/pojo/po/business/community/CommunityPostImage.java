package com.pivothub.pojo.po.business.community;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.pivothub.pojo.po.BasePo;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @Description: 社区帖子图片实体
 * @Author lhb
 * @CreateTime 2026/9/25
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class CommunityPostImage extends BasePo {
    @TableId(type = IdType.INPUT)
    private String uuid;

    /** 帖子uuid */
    private String postId;

    /** 文件uuid，对应sys_file.uuid */
    private String fileId;

    /** 图片排序 */
    private Integer sort;

    /** 状态 0-删除 1-正常 */
    private Integer status;
}
