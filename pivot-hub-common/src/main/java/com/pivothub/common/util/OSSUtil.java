package com.pivothub.common.util;

import com.aliyun.oss.ClientBuilderConfiguration;
import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClientBuilder;
import com.aliyun.oss.common.auth.DefaultCredentialProvider;
import com.aliyun.oss.common.comm.SignVersion;
import com.aliyun.oss.model.ObjectMetadata;
import com.aliyun.oss.model.PutObjectRequest;
import com.pivothub.common.config.AliyunConfig;
import com.pivothub.common.exception.SystemException;
import org.springframework.util.StringUtils;

import java.io.InputStream;
import java.util.Date;

/** 从参考商城 OSSUtil 迁移；文件登记与访问权限由后续 System 文件服务负责。 */
public final class OSSUtil {
    private static final long LEGACY_URL_TTL_MILLIS = 3600_000L;

    private OSSUtil() {}

    /** 保留原工具签名。返回临时访问 URL，文件表应保存对象路径而非此 URL。输入流由调用者关闭。 */
    public static String uploadFileToAli(String catalog, String suffixName, String objectName,
                                         InputStream in, AliyunConfig config) {
        if (!StringUtils.hasText(catalog) || !StringUtils.hasText(objectName) || suffixName == null || in == null) {
            throw new SystemException(400, "文件上传参数无效");
        }
        String objectKey = catalog + '/' + objectName + suffixName;
        validateObjectKey(objectKey);
        OSS client = getOSSObject(config);
        try {
            ObjectMetadata metadata = new ObjectMetadata();
            metadata.setHeader("x-oss-object-acl", "private");
            PutObjectRequest request = new PutObjectRequest(config.getBucketName(), objectKey, in);
            request.setMetadata(metadata);
            client.putObject(request);
            return client.generatePresignedUrl(config.getBucketName(), objectKey,
                    new Date(System.currentTimeMillis() + LEGACY_URL_TTL_MILLIS)).toExternalForm();
        } catch (RuntimeException ex) {
            throw new SystemException(503, "文件上传或签名失败");
        } finally {
            client.shutdown();
        }
    }

    public static void deleteFileFromAli(AliyunConfig config, String objectName) {
        validateObjectKey(objectName);
        OSS client = getOSSObject(config);
        try {
            // OSS 删除不存在的对象本身是幂等操作，避免检查和删除之间的竞争。
            client.deleteObject(config.getBucketName(), objectName);
        } catch (RuntimeException ex) {
            throw new SystemException(503, "文件删除失败");
        } finally {
            client.shutdown();
        }
    }

    public static String signedUrl(AliyunConfig config, String objectKey, Date expiresAt) {
        validateObjectKey(objectKey);
        if (expiresAt == null || expiresAt.getTime() <= System.currentTimeMillis()) {
            throw new SystemException(400, "文件链接有效期无效");
        }
        OSS client = getOSSObject(config);
        try {
            return client.generatePresignedUrl(config.getBucketName(), objectKey, expiresAt).toExternalForm();
        } catch (RuntimeException ex) {
            throw new SystemException(503, "文件链接签名失败");
        } finally {
            client.shutdown();
        }
    }

    /** 调用者使用完毕必须 shutdown；保留参考项目的 V4 签名和地域配置。 */
    public static OSS getOSSObject(AliyunConfig config) {
        if (config == null || !StringUtils.hasText(config.getAccessKeyId())
                || !StringUtils.hasText(config.getAccessKeySecret()) || !StringUtils.hasText(config.getEndpoint())
                || !StringUtils.hasText(config.getBucketName()) || !StringUtils.hasText(config.getRegion())) {
            throw new SystemException(503, "文件存储配置不完整");
        }
        try {
            ClientBuilderConfiguration options = new ClientBuilderConfiguration();
            options.setSignatureVersion(SignVersion.V4);
            options.setConnectionTimeout(3000);
            options.setSocketTimeout(10000);
            options.setMaxErrorRetry(1);
            return OSSClientBuilder.create().endpoint(config.getEndpoint())
                    .credentialsProvider(new DefaultCredentialProvider(config.getAccessKeyId(), config.getAccessKeySecret()))
                    .clientConfiguration(options).region(config.getRegion()).build();
        } catch (RuntimeException ex) {
            throw new SystemException(503, "文件存储暂时不可用");
        }
    }

    private static void validateObjectKey(String key) {
        if (!StringUtils.hasText(key) || key.length() > 512 || key.startsWith("/") || key.contains("\\")
                || key.contains("//") || key.chars().anyMatch(c -> c < 32 || c == 127)) {
            throw new SystemException(400, "文件存储路径无效");
        }
        for (String segment : key.split("/")) {
            if (".".equals(segment) || "..".equals(segment)) throw new SystemException(400, "文件存储路径无效");
        }
    }
}
