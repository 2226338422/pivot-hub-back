package com.pivothub.system.controller;

import com.pivothub.common.result.Result;
import com.pivothub.pojo.vo.system.file.FileUploadVo;
import com.pivothub.system.file.service.FileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

/** 文件服务接口（登录鉴权但不挂菜单权限）。 */
@RestController
@RequestMapping("/system/file")
public class FileController {

    @Autowired
    private FileService fileService;

    /**
     * 上传文件到 OSS，落 sys_file 表，返回 uuid。
     * acl=public 存公共读 bucket，acl=private 存私有 bucket。
     */
    @PostMapping("/upload")
    public Result<FileUploadVo> upload(@RequestParam("file") MultipartFile file,
                                       @RequestParam("module") String module,
                                       @RequestParam(value = "type", required = false) String type,
                                       @RequestParam(value = "acl", required = false, defaultValue = "public") String acl) {
        return Result.success(fileService.upload(file, module, type, acl));
    }

    /**
     * 批量根据文件 uuid 查询访问地址。uuids 以英文逗号分隔。
     */
    @GetMapping("/url")
    public Result<Map<String, String>> urls(@RequestParam("uuids") String uuids) {
        return Result.success(fileService.urls(uuids));
    }
}
