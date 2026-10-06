package com.pivothub.pojo.po.business.map;

import lombok.Data;
import java.math.BigDecimal;
import lombok.EqualsAndHashCode;
import com.pivothub.pojo.po.BasePo;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.IdType;

/**
 * @Description: 地图主题地点及笔记实体
 * @Author lhb
 * @CreateTime 2026/10/5
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class MapPoint extends BasePo {
    /** 地点uuid */
    @TableId(type = IdType.INPUT)
    private String uuid;

    /** 状态 0-删除 1-正常 */
    private Integer status;

    /** 地点所有者用户uuid */
    private String ownerUserId;

    /** 所属主题uuid */
    private String themeId;

    /** 所属分类uuid，为空表示未分类 */
    private String categoryId;

    /** 地点名称 */
    private String name;

    /** 地点地址描述 */
    private String address;

    /** 地点经度，GCJ-02坐标，最多保留6位小数 */
    private BigDecimal longitude;

    /** 地点纬度，GCJ-02坐标，最多保留6位小数 */
    private BigDecimal latitude;

    /** 地点文字笔记 */
    private String note;

    /** 地点来源 0-手工或地图选点 1-高德POI */
    private Integer source;

    /** 高德POI标识，手工来源为空 */
    private String amapPoiId;
}
