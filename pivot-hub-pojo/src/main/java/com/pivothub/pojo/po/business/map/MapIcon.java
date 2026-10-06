package com.pivothub.pojo.po.business.map;

import lombok.Data;
import lombok.EqualsAndHashCode;
import com.pivothub.pojo.po.BasePo;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.IdType;

/**
 * @Description: 用户私有地图图标素材实体
 * @Author lhb
 * @CreateTime 2026/10/5
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class MapIcon extends BasePo {
    /** 图标素材uuid */
    @TableId(type = IdType.INPUT)
    private String uuid;

    /** 状态 0-删除 1-正常 */
    private Integer status;

    /** 图标素材所有者用户uuid */
    private String ownerUserId;

    /** 图标素材名称 */
    private String name;

    /** OSS对象路径，不保存临时签名URL */
    private String objectKey;

    /** 文件MIME类型，例如image/png */
    private String mimeType;

    /** 文件大小，单位字节 */
    private Long fileSize;

    /** 图标显示宽度，单位像素 */
    private Integer width;

    /** 图标显示高度，单位像素 */
    private Integer height;

    /** 图标锚点，例如bottom-center表示底部中心对准地点坐标 */
    private String anchor;
}
