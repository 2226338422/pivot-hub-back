package com.pivothub.commoncore.constants.map;

/** 地图资源限制与专用 SDK 代理协议。 */
public final class MapConstants {
    private MapConstants() {}
    public static final int ACTIVE = 1;
    public static final int DELETED = 0;
    public static final int POINT_SOURCE_MANUAL = 0;
    public static final int POINT_SOURCE_AMAP = 1;
    public static final int MAX_THEME_POINTS = 5000;
    public static final int MAX_NOTE_LENGTH = 10000;
    public static final int MAX_PAGE_SIZE = 100;
    public static final String MAP_MENU_CODE = "map";
    public static final String SDK_PROXY_PREFIX = "/business/map/amap/_AMapService";
}
