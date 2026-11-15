package com.pivothub.pojo.vo.system.announcement;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

@Data
@Schema(description = "已发布公告内容流")
public class AnnouncementFeedVo {
    @Schema(description = "本批公告")
    private List<AnnouncementFeedItemVo> list;

    @Schema(description = "本批最后一条公告的UUID；没有下一批时为空")
    private String nextCursorId;

    @Schema(description = "是否还有下一批")
    private Boolean hasMore;
}
