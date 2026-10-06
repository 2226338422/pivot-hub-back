package com.pivothub.business.map.service;

import com.pivothub.pojo.dto.business.map.*;
import com.pivothub.pojo.vo.business.map.*;
import com.pivothub.pojo.vo.PageVo;
import org.springframework.web.multipart.MultipartFile;
import java.util.List;

/**
 * @Description: 私有地图主题地点分类服务接口
 * @Author lhb
 * @CreateTime 2026/10/5
 */
public interface MapCategoryService {
    /**
     * @Description: 查询当前用户指定主题下的正常地点分类
     * @param themeId 主题唯一标识
     * @return 主题地点分类列表
     */
    List<MapCategoryVo> list(String themeId);

    /**
     * @Description: 为当前用户的地图主题新增地点分类
     * @param request 分类名称、排序及所属主题
     * @return 新增后的分类信息
     */
    MapCategoryVo add(MapCategoryCreateDto request);

    /**
     * @Description: 修改当前用户的地图地点分类
     * @param request 分类标识及可编辑信息
     * @return 修改后的分类信息
     */
    MapCategoryVo update(MapCategoryUpdateDto request);

    /**
     * @Description: 逻辑删除当前用户的地点分类，有正常地点引用时拒绝删除
     * @param categoryId 分类唯一标识
     */
    void delete(String categoryId);
}
