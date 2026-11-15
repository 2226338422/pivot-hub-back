package com.pivothub.pojo.dto.system.announcement;

import com.pivothub.pojo.dto.PageDto;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@Schema(description = "公告分页查询条件")
public class AnnouncementPageDto extends PageDto {
    @Size(max = 200, message = "公告标题搜索词过长")
    @Schema(description = "公告标题，包含匹配，最多200字")
    private String title;

    @Schema(description = "公告状态，管理页可用：0-草稿，1-已发布，2-已撤回")
    private Integer status;

    @Schema(description = "发布时间排序：0-倒序，1-升序；默认倒序")
    private Integer publishTimeOrder = 0;

    @Schema(description = "创建时间排序：null-不参与，0-倒序，1-升序")
    private Integer createTimeOrder;

    @Schema(description = "更新时间排序：null-不参与，0-倒序，1-升序")
    private Integer updateTimeOrder;
}
