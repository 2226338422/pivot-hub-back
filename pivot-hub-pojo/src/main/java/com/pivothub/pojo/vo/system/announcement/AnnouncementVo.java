package com.pivothub.pojo.vo.system.announcement;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.Date;
import java.util.List;

@Data
@Schema(description = "公告信息")
public class AnnouncementVo {
    @Schema(description = "公告唯一标识")
    private String uuid;

    @Schema(description = "公告标题")
    private String title;

    @Schema(description = "公告正文")
    private String content;

    @Schema(description = "状态：0-草稿，1-已发布，2-已撤回")
    private Integer status;

    @Schema(description = "发布人用户唯一标识，由服务端登录上下文确定")
    private String publisherId;

    @Schema(description = "发布人昵称")
    private String publisherName;

    @Schema(description = "首次发布时间")
    private Date publishTime;

    @Schema(description = "创建时间")
    private Date createTime;

    @Schema(description = "更新时间")
    private Date updateTime;

    @Schema(description = "按展示顺序排列的附件UUID，不包含访问地址")
    private List<String> fileIds;
}
