package com.pivothub.pojo.dto.system.announcement;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Size;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
@Schema(description = "已发布公告内容流查询")
public class AnnouncementFeedQueryDto {
    @Size(max = 200, message = "公告标题搜索词过长")
    @Schema(description = "公告标题，包含匹配，最多200字")
    private String title;

    @Schema(description = "发布时间排序：0-倒序，1-升序；默认倒序")
    private Integer publishTimeOrder = 0;

    @Schema(description = "上一批最后一条公告的UUID；首次查询不传")
    private String cursorId;

    @NotNull(message = "每批公告条数不能为空")
    @Min(value = 10, message = "每批公告至少10条")
    @Max(value = 50, message = "每批公告最多50条")
    @Schema(description = "每批条数，范围10到50，默认10")
    private Integer limit = 10;
}
