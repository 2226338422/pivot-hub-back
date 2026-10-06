package com.pivothub.pojo.po.business.map;

import lombok.Data;
import java.math.BigDecimal;
import lombok.EqualsAndHashCode;
import com.pivothub.pojo.po.BasePo;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.IdType;

/**
 * @Description: 私有地图主题实体
 * @Author lhb
 * @CreateTime 2026/10/5
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class MapTheme extends BasePo {
    /** 主题uuid */
    @TableId(type = IdType.INPUT)
    private String uuid;

    /** 状态 0-删除 1-正常 */
    private Integer status;

    /** 主题所有者用户uuid */
    private String ownerUserId;

    /** 主题名称 */
    private String name;

    /** 主题描述 */
    private String description;

    /** 主题中心经度，GCJ-02坐标，允许与纬度同时为空 */
    private BigDecimal centerLongitude;

    /** 主题中心纬度，GCJ-02坐标，允许与经度同时为空 */
    private BigDecimal centerLatitude;

    /** 默认地图缩放级别，取值3至20 */
    private Integer defaultZoom;

    /** 展示排序，数值越小越靠前 */
    private Integer sort;
}
