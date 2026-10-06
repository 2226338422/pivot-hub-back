package com.pivothub.business.map.service;

import com.pivothub.pojo.dto.business.map.*;
import com.pivothub.pojo.vo.business.map.*;
import com.pivothub.pojo.vo.PageVo;
import org.springframework.web.multipart.MultipartFile;
import java.util.List;

/**
 * @Description: 私有地图地点与文字笔记服务接口
 * @Author lhb
 * @CreateTime 2026/10/5
 */
public interface MapPointService {
    /**
     * @Description: 按主题、名称及分类条件分页查询当前用户已保存地点
     * @param query 分页参数及地点筛选条件
     * @return 通用分页地点摘要，不包含文字笔记
     */
    PageVo<MapPointSummaryVo> page(MapPointPageDto query);

    /**
     * @Description: 按主题和筛选条件查询地图地点标记
     * @param query 主题、名称及分类筛选条件
     * @return 轻量地点摘要列表，不包含文字笔记
     */
    List<MapPointSummaryVo> markers(MapPointQueryDto query);

    /**
     * @Description: 查询当前用户的地点完整详情
     * @param pointId 地点唯一标识
     * @return 包含文字笔记的地点详情
     */
    MapPointVo detail(String pointId);

    /**
     * @Description: 为当前用户的主题新增地点并保存文字笔记
     * @param request 主题、分类、名称、地址、坐标、来源及笔记
     * @return 保存后的地点完整详情
     */
    MapPointVo add(MapPointCreateDto request);

    /**
     * @Description: 修改当前用户的地点信息及文字笔记
     * @param request 地点标识及可编辑字段
     * @return 修改后的地点完整详情
     */
    MapPointVo update(MapPointUpdateDto request);

    /**
     * @Description: 逻辑删除当前用户的地图地点及其文字笔记
     * @param pointId 地点唯一标识
     */
    void delete(String pointId);
}
