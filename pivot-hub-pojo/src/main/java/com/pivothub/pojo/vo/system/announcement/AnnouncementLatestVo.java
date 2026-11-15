package com.pivothub.pojo.vo.system.announcement;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.Date;

@Data
@Schema(description = "最近发布的公告标题与时间")
public class AnnouncementLatestVo {
    @Schema(description = "公告标题")
    private String title;

    @Schema(description = "发布时间")
    private Date publishTime;
}
