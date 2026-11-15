package com.pivothub.pojo.po.system;

import com.pivothub.pojo.po.BasePo;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Date;

@Data
@EqualsAndHashCode(callSuper = true)
public class SysAnnouncement extends BasePo {
    /** 公告唯一标识。 */
    private String uuid;

    /** 公告标题。 */
    private String title;

    /** 公告正文。 */
    private String content;

    /** 状态：0-草稿，1-已发布，2-已撤回。 */
    private Integer status;

    /** 发布人的用户唯一标识。 */
    private String publisherId;

    /** 首次发布时间。 */
    private Date publishTime;
}
