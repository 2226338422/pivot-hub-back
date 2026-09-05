package com.pivothub.pojo.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * @Description: 分页请求DTO
 * @Author: lhb
 */
@Data
public class PageDto {
    @Schema(description = "当前页码")
    private Integer pageNum = 1;
    @Schema(description = "每页条数")
    private Integer pageSize = 10;
}
