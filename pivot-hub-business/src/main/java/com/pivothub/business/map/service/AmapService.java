package com.pivothub.business.map.service;

import com.pivothub.pojo.dto.business.map.AmapCoordinateDto;
import com.pivothub.pojo.dto.business.map.AmapPoiIdDto;
import com.pivothub.pojo.dto.business.map.AmapPoiPageDto;
import com.pivothub.pojo.dto.business.map.AmapRouteRequestDto;
import com.pivothub.pojo.dto.business.map.AmapSearchRequestDto;
import com.pivothub.pojo.vo.business.map.MapCityOptionVo;
import com.pivothub.pojo.vo.business.map.MapCityVo;
import com.pivothub.pojo.vo.business.map.MapRouteVo;
import com.pivothub.pojo.vo.business.map.MapReverseGeocodeVo;
import com.pivothub.pojo.vo.PageVo;
import com.pivothub.pojo.vo.business.map.SearchPoiVo;
import com.pivothub.pojo.vo.business.map.SearchTipVo;
import jakarta.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * @Description: 高德地图城市、地点及路线查询服务接口
 * @Author lhb
 * @CreateTime 2026/10/5
 */
public interface AmapService {
    /**
     * @Description: 解析可信客户端IP并查询网络所在城市
     * @param request 原始请求，用于解析可信代理链中的客户端IP
     * @param operatorId 当前登录用户标识
     * @return 网络城市识别结果，无法识别时available为false
     */
    MapCityVo city(HttpServletRequest request, String operatorId);

    /**
     * @Description: 查询国内行政区划并生成城市选择列表
     * @param operatorId 当前登录用户标识
     * @return 服务可提供的城市及县级市选项列表
     */
    List<MapCityOptionVo> cities(String operatorId);

    /**
     * @Description: 按关键词和可选城市条件查询地址输入提示
     * @param request 关键词及城市范围条件
     * @param operatorId 当前登录用户标识
     * @return 地址输入提示候选列表
     */
    List<SearchTipVo> searchTips(AmapSearchRequestDto request, String operatorId);

    /**
     * @Description: 按关键词和可选城市范围分页查询高德地点
     * @param request 地点搜索条件及分页参数
     * @param operatorId 当前登录用户标识
     * @return 通用分页结果，当前页地点位于list字段
     */
    PageVo<SearchPoiVo> searchPois(AmapPoiPageDto request, String operatorId);

    /**
     * @Description: 根据高德POI标识查询具体地点详情
     * @param request 高德POI标识请求
     * @param operatorId 当前登录用户标识
     * @return 地点名称、地址及标准坐标，无匹配地点时为空
     */
    SearchPoiVo poiDetail(AmapPoiIdDto request, String operatorId);

    /**
     * @Description: 根据点击坐标查询地址及附近地点候选
     * @param request 用户点击位置的经纬度
     * @param operatorId 当前登录用户标识
     * @return 原点击坐标、地址描述及最多10个附近地点候选
     */
    MapReverseGeocodeVo reverseGeocode(AmapCoordinateDto request, String operatorId);

    /**
     * @Description: 根据用户明确选择的起终点和出行方式规划路线
     * @param request 起终点坐标、出行方式及可选公交城市编码
     * @param operatorId 当前登录用户标识
     * @return 路线分段折线、距离、耗时和步骤，无可用路线时返回空路线
     */
    MapRouteVo route(AmapRouteRequestDto request, String operatorId);
}
