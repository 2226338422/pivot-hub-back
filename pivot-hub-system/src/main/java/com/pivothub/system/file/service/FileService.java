package com.pivothub.system.file.service;

import com.pivothub.pojo.vo.system.file.FileUploadVo;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

/** 文件服务：上传落库、uuid → 公共读 URL 查询。 */
public interface FileService {

    /**
     * 上传文件到 OSS 并登记到 sys_file 表。
     *
     * @param file   multipart 文件
     * @param module 业务模块/目录（如 Avatar）
     * @param type   文件类型（如 avatar；可空）
     * @param acl    访问模式 public/private，null 按 public
     * @return 上传结果，含 uuid
     */
    FileUploadVo upload(MultipartFile file, String module, String type, String acl);

    /**
     * 批量获取文件访问地址。uuids 以英文逗号分隔，内部去重、过滤空串。
     * 返回 uuid -> url 映射，动态读 OSS 配置拼接。
     */
    Map<String, String> urls(String uuids);

    /**
     * 单个文件访问地址，缺省返回空串。
     */
    String url(String uuid);

    /**
     * 默认头像的文件 uuid（查 sys_file type=default_avatar），无则返回空串。
     */
    String defaultAvatarFileId();
}
