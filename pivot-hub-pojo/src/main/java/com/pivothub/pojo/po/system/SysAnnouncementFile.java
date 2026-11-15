package com.pivothub.pojo.po.system;

import com.pivothub.pojo.po.BasePo;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class SysAnnouncementFile extends BasePo {
    /** 关联记录唯一标识。 */
    private String uuid;

    /** 公告唯一标识。 */
    private String announcementId;

    /** sys_file 中的附件唯一标识。 */
    private String fileId;

    /** 图片展示顺序。 */
    private Integer sort;
}
