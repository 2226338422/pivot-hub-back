package com.pivothub.pojo.vo.system.file;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/** 文件上传结果。后端只返回 uuid，访问地址由前端通过 uuid 调 /system/file/url 获取。 */
@Data
@Schema(description = "文件上传结果")
public class FileUploadVo {
    @Schema(description = "文件唯一标识，业务表关联此值")
    private String uuid;
}
