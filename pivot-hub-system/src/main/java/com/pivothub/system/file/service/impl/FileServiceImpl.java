package com.pivothub.system.file.service.impl;

import com.pivothub.common.config.AliyunConfig;
import com.pivothub.common.exception.BusinessException;
import com.pivothub.common.util.OSSUtil;
import com.pivothub.common.util.TLUtil;
import com.pivothub.commoncore.enums.FileType;
import com.pivothub.pojo.po.system.SysFile;
import com.pivothub.pojo.po.system.SysUser;
import com.pivothub.pojo.vo.system.file.FileUploadVo;
import com.pivothub.system.file.service.FileService;
import com.pivothub.system.mapper.SysFileMapper;
import com.pivothub.system.mapper.SysUserMapper;
import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
public class FileServiceImpl implements FileService {
    private static final Logger LOG = LoggerFactory.getLogger(FileServiceImpl.class);

    private static final java.util.regex.Pattern MODULE_PATTERN = java.util.regex.Pattern.compile("^[A-Za-z0-9_-]{1,50}$");
    private static final String ACL_PUBLIC = "public";
    private static final String ACL_PRIVATE = "private";

    @Autowired
    private AliyunConfig aliyunConfig;

    @Autowired
    private SysFileMapper fileMapper;

    @Autowired
    private SysUserMapper userMapper;

    @Override
    public FileUploadVo upload(MultipartFile file, String module, String type, String acl) {
        if (file == null || file.isEmpty()) {
            throw new BusinessException(400, "文件不能为空");
        }
        if (!StringUtils.hasText(module) || !MODULE_PATTERN.matcher(module).matches()) {
            throw new BusinessException(400, "文件模块参数无效");
        }
        boolean isPrivate = ACL_PRIVATE.equalsIgnoreCase(acl);
        String aclValue = isPrivate ? ACL_PRIVATE : ACL_PUBLIC;

        String originalName = file.getOriginalFilename();
        String suffixName = extractSuffixWithDot(originalName);
        String extension = suffixName.isEmpty() ? "" : suffixName.substring(1);
        String fileUuid = IdWorker.getIdStr();

        String objectKey;
        try (InputStream in = file.getInputStream()) {
            objectKey = OSSUtil.uploadFileToAli(module, suffixName, fileUuid, in, aliyunConfig, isPrivate);
        } catch (IOException ex) {
            LOG.warn("读取上传文件流失败：{}", ex.getMessage());
            throw new BusinessException(503, "文件上传失败");
        }

        String userId = currentUserId();
        SysFile record = new SysFile();
        record.setUuid(fileUuid);
        record.setFileName(originalName);
        record.setFilePath(objectKey);
        record.setFileSize(file.getSize());
        record.setFileType(file.getContentType());
        record.setExtension(extension);
        record.setModule(module);
        record.setType(StringUtils.hasText(type) ? type : null);
        record.setAcl(aclValue);
        record.setUploaderId(userId);
        record.setUploaderName(resolveUploaderName(userId));
        record.setStatus(1);
        record.setCreateUserId(userId);
        record.setUpdateUserId(userId);
        fileMapper.insert(record);

        FileUploadVo vo = new FileUploadVo();
        vo.setUuid(fileUuid);
        LOG.info("文件上传成功 uuid={} objectKey={} acl={} module={} uploader={}", fileUuid, objectKey, aclValue, module, userId);
        return vo;
    }

    @Override
    public Map<String, String> urls(String uuids) {
        List<String> distinct = parseUuids(uuids);
        if (distinct.isEmpty()) {
            return Collections.emptyMap();
        }
        List<SysFile> files = fileMapper.selectByUuids(distinct);
        Map<String, String> result = new LinkedHashMap<>();
        for (SysFile file : files) {
            String url = resolveUrl(file);
            if (url != null) {
                result.put(file.getUuid(), url);
            }
        }
        return result;
    }

    @Override
    public String url(String uuid) {
        if (!StringUtils.hasText(uuid)) {
            return "";
        }
        Map<String, String> map = urls(uuid);
        return map.getOrDefault(uuid, "");
    }

    @Override
    public String defaultAvatarFileId() {
        SysFile file = fileMapper.selectFirstByType(FileType.DEFAULT_AVATAR.getValue());
        return file == null ? "" : file.getUuid();
    }

    /** 解析逗号分隔的 uuid 串，去重、过滤空串。 */
    private List<String> parseUuids(String uuids) {
        if (!StringUtils.hasText(uuids)) {
            return Collections.emptyList();
        }
        return Arrays.stream(uuids.split(","))
                .map(String::trim)
                .filter(StringUtils::hasText)
                .distinct()
                .toList();
    }

    /** 按 acl 分派：public 走 publicUrl（直链），private 走 signedUrl（带时效）。 */
    private String resolveUrl(SysFile file) {
        String objectKey = file.getFilePath();
        if (!StringUtils.hasText(objectKey)) {
            return null;
        }
        if (ACL_PRIVATE.equalsIgnoreCase(file.getAcl())) {
            return OSSUtil.signedUrl(aliyunConfig, objectKey);
        }
        return OSSUtil.publicUrl(aliyunConfig, objectKey);
    }

    private String currentUserId() {
        String userId = TLUtil.get(TLUtil.USER_ID);
        if (!StringUtils.hasText(userId)) {
            throw new BusinessException(500, "用户身份上下文不可用");
        }
        return userId;
    }

    private String resolveUploaderName(String userId) {
        try {
            SysUser user = userMapper.selectById(userId);
            return user == null ? null : (StringUtils.hasText(user.getNickname()) ? user.getNickname() : user.getCode());
        } catch (RuntimeException ex) {
            LOG.warn("解析上传者名称失败 userId={}: {}", userId, ex.getMessage());
            return null;
        }
    }

    private String extractSuffixWithDot(String fileName) {
        if (!StringUtils.hasText(fileName)) {
            return "";
        }
        int slash = Math.max(fileName.lastIndexOf('/'), fileName.lastIndexOf('\\'));
        String base = slash >= 0 ? fileName.substring(slash + 1) : fileName;
        int dot = base.lastIndexOf('.');
        if (dot < 0 || dot == base.length() - 1) {
            return "";
        }
        return base.substring(dot).toLowerCase();
    }
}
