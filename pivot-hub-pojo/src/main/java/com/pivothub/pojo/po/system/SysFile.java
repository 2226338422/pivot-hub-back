package com.pivothub.pojo.po.system;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.pivothub.pojo.po.BasePo;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @Description: 文件实体
 * @Author lhb
 * @CreateTime 2026/9/22 22:42
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class SysFile extends BasePo {
    @TableId(type = IdType.INPUT)
    private String uuid;

    /** 原始文件名 */
    private String fileName;

    /** 文件存储路径（OSS objectKey） */
    private String filePath;

    /** 文件大小（字节） */
    private Long fileSize;

    /** 文件类型（MIME） */
    private String fileType;

    /** 文件扩展名 */
    private String extension;

    /** 所属业务模块 */
    private String module;

    /** 文件类型（avatar-头像 default_avatar-默认头像） */
    private String type;

    /** 访问模式 public-公共读 private-私有读 */
    private String acl;

    /** 上传者 uuid */
    private String uploaderId;

    /** 上传者名称 */
    private String uploaderName;

    /** 状态 0-删除 1-正常 */
    private Integer status;
}
