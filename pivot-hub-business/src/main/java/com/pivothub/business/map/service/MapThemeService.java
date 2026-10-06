package com.pivothub.business.map.service;

import com.pivothub.pojo.dto.business.map.*;
import com.pivothub.pojo.vo.business.map.*;
import com.pivothub.pojo.vo.PageVo;
import org.springframework.web.multipart.MultipartFile;
import java.util.List;

/**
 * @Description: 私有地图主题服务接口
 * @Author lhb
 * @CreateTime 2026/10/5
 */
public interface MapThemeService {
    /**
     * @Description: 查询当前用户的正常地图主题列表
     * @return 按展示排序排列的本人主题列表
     */
    List<MapThemeVo> list();

    /**
     * @Description: 查询当前用户的地图主题详情
     * @param themeId 主题唯一标识
     * @return 主题名称、描述、中心坐标及默认缩放等信息
     */
    MapThemeVo detail(String themeId);

    /**
     * @Description: 为当前用户创建私有地图主题
     * @param request 主题名称、描述、可选中心及默认缩放
     * @return 创建后的主题信息
     */
    MapThemeVo add(MapThemeCreateDto request);

    /**
     * @Description: 修改当前用户的私有地图主题
     * @param request 主题标识及可编辑字段
     * @return 修改后的主题信息
     */
    MapThemeVo update(MapThemeUpdateDto request);

    /**
     * @Description: 逻辑删除当前用户的主题及其分类、地点和笔记
     * @param themeId 主题唯一标识
     */
    void delete(String themeId);
}
