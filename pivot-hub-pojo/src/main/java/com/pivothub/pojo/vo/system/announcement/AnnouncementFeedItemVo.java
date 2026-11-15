package com.pivothub.pojo.vo.system.announcement;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.Date;
import java.util.List;

@Data
@Schema(description = "公开公告列表项")
public class AnnouncementFeedItemVo {
    @Schema(description = "公告唯一标识")
    private String uuid;

    @Schema(description = "公告标题")
    private String title;

    @Schema(description = "公告正文")
    private String content;

    @Schema(description = "发布时间")
    private Date publishTime;

    @Schema(description = "按展示顺序排列的附件UUID，不包含访问地址")
    private List<String> fileIds;
}
