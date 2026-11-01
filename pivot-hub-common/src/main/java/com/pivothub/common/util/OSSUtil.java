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

/**
 * 阿里云 OSS 工具：支持公共读 bucket 与私有 bucket。
 * 共用 AK/SK/endpoint/region，按 isPrivate 选择 bucket。
 */
public final class OSSUtil {
    private OSSUtil() {}

    /** 上传到公共读 bucket（不设对象 ACL，继承 bucket 公共读）。返回 objectKey。 */
    public static String uploadFileToAli(String catalog, String suffixName, String objectName,
                                         InputStream in, AliyunConfig config) {
        return upload(catalog, suffixName, objectName, in, config, false);
    }

    /** 上传到私有 bucket（对象 ACL = private）。返回 objectKey。 */
    public static String uploadFileToAli(String catalog, String suffixName, String objectName,
                                         InputStream in, AliyunConfig config, boolean isPrivate) {
        return upload(catalog, suffixName, objectName, in, config, isPrivate);
    }

    private static String upload(String catalog, String suffixName, String objectName,
                                 InputStream in, AliyunConfig config, boolean isPrivate) {
        if (!StringUtils.hasText(catalog) || !StringUtils.hasText(objectName) || suffixName == null || in == null) {
            throw new SystemException(400, "文件上传参数无效");
        }
        String objectKey = catalog + '/' + objectName + suffixName;
        validateObjectKey(objectKey);
        String bucketName = isPrivate ? config.getPrivateBucket() : config.getPublicBucket();
        if (!StringUtils.hasText(bucketName)) {
            throw new SystemException(503, isPrivate ? "私有 bucket 未配置" : "公共 bucket 未配置");
        }
        OSS client = getOSSObject(config);
        try {
            PutObjectRequest request = new PutObjectRequest(bucketName, objectKey, in);
            if (isPrivate) {
                ObjectMetadata metadata = new ObjectMetadata();
                metadata.setHeader("x-oss-object-acl", "private");
                request.setMetadata(metadata);
            }
            client.putObject(request);
            return objectKey;
        } catch (RuntimeException ex) {
            throw new SystemException(503, "文件上传失败");
        } finally {
            client.shutdown();
        }
    }

    /** 删除对象。isPrivate 指定 bucket。 */
    public static void deleteFileFromAli(AliyunConfig config, String objectKey, boolean isPrivate) {
        validateObjectKey(objectKey);
        String bucketName = isPrivate ? config.getPrivateBucket() : config.getPublicBucket();
        if (!StringUtils.hasText(bucketName)) {
            throw new SystemException(503, isPrivate ? "私有 bucket 未配置" : "公共 bucket 未配置");
        }
        OSS client = getOSSObject(config);
        try {
            client.deleteObject(bucketName, objectKey);
        } catch (RuntimeException ex) {
            throw new SystemException(503, "文件删除失败");
        } finally {
            client.shutdown();
        }
    }

    /** 公共读对象的访问地址：https://{bucket}.{endpoint-host}/{objectKey}。 */
    public static String publicUrl(AliyunConfig config, String objectKey) {
        if (!StringUtils.hasText(objectKey)) {
            return null;
        }
        String bucketName = config.getPublicBucket();
        return buildUrl(config, bucketName, objectKey);
    }

    /** 私有对象的签名 URL，按 config.privateUrlTtlSeconds 过期。 */
    public static String signedUrl(AliyunConfig config, String objectKey) {
        long ttlSeconds = config.getPrivateUrlTtlSeconds() != null ? config.getPrivateUrlTtlSeconds() : 21600L;
        return signedUrl(config, objectKey, new Date(System.currentTimeMillis() + ttlSeconds * 1000));
    }

    /** 私有对象的签名 URL，自定义过期时间。 */
    public static String signedUrl(AliyunConfig config, String objectKey, Date expiresAt) {
        validateObjectKey(objectKey);
        if (expiresAt == null || expiresAt.getTime() <= System.currentTimeMillis()) {
            throw new SystemException(400, "文件链接有效期无效");
        }
        String bucketName = config.getPrivateBucket();
        if (!StringUtils.hasText(bucketName)) {
            throw new SystemException(503, "私有 bucket 未配置");
        }
        OSS client = getOSSObject(config);
        try {
            return client.generatePresignedUrl(bucketName, objectKey, expiresAt).toExternalForm();
        } catch (RuntimeException ex) {
            throw new SystemException(503, "文件链接签名失败");
        } finally {
            client.shutdown();
        }
    }

    private static String buildUrl(AliyunConfig config, String bucketName, String objectKey) {
        if (config == null || !StringUtils.hasText(bucketName) || !StringUtils.hasText(config.getEndpoint())) {
            throw new SystemException(503, "文件存储配置不完整");
        }
        String host = config.getEndpoint();
        int schemeIdx = host.indexOf("://");
        if (schemeIdx >= 0) {
            host = host.substring(schemeIdx + 3);
        }
        if (host.endsWith("/")) {
            host = host.substring(0, host.length() - 1);
        }
        if (objectKey.startsWith("/")) {
            objectKey = objectKey.substring(1);
        }
        return "https://" + bucketName + "." + host + "/" + objectKey;
    }

    public static OSS getOSSObject(AliyunConfig config) {
        if (config == null || !StringUtils.hasText(config.getAccessKeyId())
                || !StringUtils.hasText(config.getAccessKeySecret()) || !StringUtils.hasText(config.getEndpoint())
                || !StringUtils.hasText(config.getRegion())) {
            throw new SystemException(503, "文件存储配置不完整");
        }
        ClientBuilderConfiguration options = new ClientBuilderConfiguration();
        options.setSignatureVersion(SignVersion.V4);
        options.setConnectionTimeout(3000);
        options.setSocketTimeout(10000);
        options.setMaxErrorRetry(1);
        return OSSClientBuilder.create().endpoint(config.getEndpoint())
                .credentialsProvider(new DefaultCredentialProvider(config.getAccessKeyId(), config.getAccessKeySecret()))
                .clientConfiguration(options).region(config.getRegion()).build();
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
