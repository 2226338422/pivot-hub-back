package com.pivothub.pojo.dto.system.notify;

import com.pivothub.pojo.dto.PageDto;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;
import java.util.List;

/** 站内信分页查询条件。 */
@Data
@EqualsAndHashCode(callSuper = true)
@Schema(title = "站内信分页查询")
public class InboxMessagePageDto extends PageDto {
    @Schema(description = "消息类型应用编码，可多选，留空表示全部")
    private List<String> messageTypes;

    @Schema(description = "站内信阅读状态，true-已读，false-未读，留空表示全部")
    private Boolean read;

    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Schema(description = "创建时间起点，含该时刻，格式为 yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTimeStart;

    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Schema(description = "创建时间终点，含该时刻，格式为 yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTimeEnd;

    @Schema(description = "创建时间排序，0-倒序，1-升序，默认倒序")
    private Integer createTimeOrder = 0;
}
