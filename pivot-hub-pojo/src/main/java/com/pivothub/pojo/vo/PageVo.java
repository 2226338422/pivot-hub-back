package com.pivothub.pojo.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

/**
 * @Description: 分页响应VO
 * @Author: lhb
 */
@Data
public class PageVo<T> {
    @Schema(description = "当前页码")
    private Integer pageNum;
    @Schema(description = "每页条数")
    private Integer pageSize;
    @Schema(description = "总记录数")
    private Long total;
    @Schema(description = "总页数")
    private Long pages;
    @Schema(description = "数据列表")
    private List<T> list;

    public static <T> PageVo<T> of(Integer pageNum, Integer pageSize, Long total, List<T> list) {
        PageVo<T> vo = new PageVo<>();
        vo.setPageNum(pageNum);
        vo.setPageSize(pageSize);
        vo.setTotal(total);
        vo.setList(list);
        vo.setPages((total + pageSize - 1) / pageSize);
        return vo;
    }
}
