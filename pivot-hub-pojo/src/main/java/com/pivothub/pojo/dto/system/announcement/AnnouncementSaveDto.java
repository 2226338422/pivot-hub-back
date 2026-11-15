package com.pivothub.pojo.dto.system.announcement;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
@Schema(description = "创建或编辑公告草稿")
public class AnnouncementSaveDto {
    @NotBlank(message = "请输入公告标题")
    @Size(max = 20, message = "公告标题最多20字")
    @Schema(description = "公告标题，最多20字")
    private String title;

    @NotBlank(message = "请输入公告正文")
    @Size(max = 500, message = "公告正文最多500字")
    @Schema(description = "公告正文，最多500字")
    private String content;

    @Size(max = 9, message = "公告图片最多9张")
    @Schema(description = "有序的公告附件UUID列表，最多9张")
    private List<String> fileIds = new ArrayList<>();
}
