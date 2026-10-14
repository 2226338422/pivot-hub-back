package com.pivothub.business.map.service.impl;

import org.springframework.beans.BeanUtils;
import com.pivothub.common.util.TLUtil;
import org.springframework.beans.factory.annotation.Autowired;

import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.pivothub.business.map.mapper.*;
import com.pivothub.business.map.service.MapPointService;
import com.pivothub.common.exception.BusinessException;
import com.pivothub.pojo.dto.business.map.*;
import com.pivothub.pojo.po.business.map.*;
import com.pivothub.pojo.vo.PageVo;
import com.pivothub.pojo.vo.business.map.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.annotation.Isolation;
import java.util.List;
import java.util.Map;
import static com.pivothub.commoncore.constants.map.MapConstants.*;

@Service
public class MapPointServiceImpl implements MapPointService {
    @Autowired
    private MapPointMapper pointMapper;
    @Autowired
    private MapThemeMapper themeMapper;
    @Autowired
    private MapCategoryMapper categoryMapper;

    @Override
    public PageVo<MapPointSummaryVo> page(MapPointPageDto query) {
        String owner = TLUtil.get(TLUtil.USER_ID);
        if (owner == null || owner.isBlank()) throw new BusinessException(500, "用户身份上下文不可用");
        if (query.getPageNum() == null || query.getPageNum() < 1 || query.getPageSize() == null
                || query.getPageSize() < 1 || query.getPageSize() > MAX_PAGE_SIZE)
            throw new BusinessException(400, "页码至少为1，每页条数为1到100");
        MapPointQueryDto filter = new MapPointQueryDto();
        BeanUtils.copyProperties(query, filter);
        validateQuery(owner, filter);
        PageHelper.startPage(query.getPageNum(), query.getPageSize());
        try {
            List<MapPointSummaryVo> points = pointMapper.selectSummaries(owner, filter);
            return PageVo.of(query.getPageNum(), query.getPageSize(), new PageInfo<>(points).getTotal(), points);
        } finally {
            PageHelper.clearPage();
        }
    }

    @Override
    public List<MapPointSummaryVo> markers(MapPointQueryDto query) {
        String owner = TLUtil.get(TLUtil.USER_ID);
        if (owner == null || owner.isBlank()) throw new BusinessException(500, "用户身份上下文不可用");
        validateQuery(owner, query);
        return pointMapper.selectMarkers(owner, query);
    }

    @Override
    public MapPointVo detail(String pointId) {
        String owner = TLUtil.get(TLUtil.USER_ID);
        if (owner == null || owner.isBlank()) throw new BusinessException(500, "用户身份上下文不可用");
        if (pointId == null || pointId.isBlank() || pointId.length() > 32) throw new BusinessException(400, "资源ID无效");
        MapPointVo result = pointMapper.selectDetail(owner, pointId);
        if (result == null) throw new BusinessException(404, "资源不存在");
        return result;
    }

    @Override
    @Transactional(rollbackFor = Exception.class, isolation = Isolation.READ_COMMITTED)
    public MapPointVo add(MapPointCreateDto request) {
        String owner = TLUtil.get(TLUtil.USER_ID);
        if (owner == null || owner.isBlank()) throw new BusinessException(500, "用户身份上下文不可用");
        if (request.getThemeId() == null || request.getThemeId().isBlank() || request.getThemeId().length() > 32) throw new BusinessException(400, "资源ID无效");
        lockTheme(owner, request.getThemeId());
        MapPoint po = new MapPoint();
        BeanUtils.copyProperties(request, po);
        validateEditable(owner, po);
        Integer source = po.getSource();
        if (source == null || (source != POINT_SOURCE_MANUAL && source != POINT_SOURCE_AMAP))
            throw new BusinessException(400, "地点来源无效");
        if (source == POINT_SOURCE_AMAP) {
            if (po.getAmapPoiId() == null || po.getAmapPoiId().trim().isEmpty() || po.getAmapPoiId().length() > 100)
                throw new BusinessException(400, "高德地点必须提供有效POI ID");
            po.setAmapPoiId(po.getAmapPoiId().trim());
            String existingId = pointMapper.findPoiId(owner, po.getThemeId(), po.getAmapPoiId());
            if (existingId != null) throw new BusinessException(409, "该主题下已存在此高德地点",
                    Map.of("existingPointId", existingId));
        } else {
            po.setAmapPoiId(null);
        }
        if (pointMapper.countThemePoints(owner, po.getThemeId()) >= MAX_THEME_POINTS)
            throw new BusinessException(409, "单个主题最多保存5000个地点");
        po.setUuid(IdWorker.getIdStr());
        po.setOwnerUserId(owner);
        po.setStatus(ACTIVE);
        po.setCreateUserId(owner);
        po.setUpdateUserId(owner);
        if (pointMapper.insert(po) != 1) throw new BusinessException(409, "资源已变更，请重新加载");
        MapPointVo result = pointMapper.selectDetail(owner, po.getUuid());
        if (result == null) throw new BusinessException(404, "资源不存在");
        return result;
    }

    @Override
    @Transactional(rollbackFor = Exception.class, isolation = Isolation.READ_COMMITTED)
    public MapPointVo update(MapPointUpdateDto request) {
        String owner = TLUtil.get(TLUtil.USER_ID);
        if (owner == null || owner.isBlank()) throw new BusinessException(500, "用户身份上下文不可用");
        if (request.getPointId() == null || request.getPointId().isBlank() || request.getPointId().length() > 32) throw new BusinessException(400, "资源ID无效");
        MapPoint original = pointMapper.selectById(owner, request.getPointId());
        if (original == null) throw new BusinessException(404, "资源不存在");
        if (!Integer.valueOf(ACTIVE).equals(original.getStatus())) throw new BusinessException(404, "资源不存在");
        lockTheme(owner, original.getThemeId());
        MapPoint po = pointMapper.selectByIdForUpdate(owner, original.getUuid());
        if (po == null) throw new BusinessException(404, "资源不存在");
        if (!Integer.valueOf(ACTIVE).equals(po.getStatus())) throw new BusinessException(404, "资源不存在");
        BeanUtils.copyProperties(request, po);
        validateEditable(owner, po);
        po.setUpdateUserId(owner);
        if (pointMapper.update(po) != 1) throw new BusinessException(409, "资源已变更，请重新加载");
        MapPointVo result = pointMapper.selectDetail(owner, po.getUuid());
        if (result == null) throw new BusinessException(404, "资源不存在");
        return result;
    }

    @Override
    @Transactional(rollbackFor = Exception.class, isolation = Isolation.READ_COMMITTED)
    public void delete(String pointId) {
        String owner = TLUtil.get(TLUtil.USER_ID);
        if (owner == null || owner.isBlank()) throw new BusinessException(500, "用户身份上下文不可用");
        if (pointId == null || pointId.isBlank() || pointId.length() > 32) throw new BusinessException(400, "资源ID无效");
        MapPoint original = pointMapper.selectById(owner, pointId);
        if (original == null) throw new BusinessException(404, "资源不存在");
        MapTheme theme = themeMapper.selectByIdForUpdate(owner, original.getThemeId());
        if (theme == null) throw new BusinessException(404, "资源不存在");
        MapPoint po = pointMapper.selectByIdForUpdate(owner, pointId);
        if (po == null) throw new BusinessException(404, "资源不存在");
        if (Integer.valueOf(DELETED).equals(po.getStatus())) return;
        if (!Integer.valueOf(ACTIVE).equals(theme.getStatus())) throw new BusinessException(404, "资源不存在");
        if (!Integer.valueOf(ACTIVE).equals(po.getStatus())) throw new BusinessException(404, "资源不存在");
        if (pointMapper.markDeleted(owner, pointId) != 1) throw new BusinessException(409, "资源已变更，请重新加载");
    }

    private void lockTheme(String owner, String themeId) {
        MapTheme theme = themeMapper.selectByIdForUpdate(owner, themeId);
        if (theme == null) throw new BusinessException(404, "资源不存在");
        if (!Integer.valueOf(ACTIVE).equals(theme.getStatus())) throw new BusinessException(404, "资源不存在");
    }

    private void requireCategory(String owner, String themeId, String categoryId) {
        if (categoryId == null) return;
        MapCategory category = categoryMapper.selectById(owner, categoryId);
        if (category == null) throw new BusinessException(404, "资源不存在");
        if (!Integer.valueOf(ACTIVE).equals(category.getStatus())) throw new BusinessException(404, "资源不存在");
        if (!themeId.equals(category.getThemeId())) throw new BusinessException(404, "资源不存在");
    }

    private void validateEditable(String owner, MapPoint po) {
        String name = po.getName();
        if (name == null || name.trim().isEmpty() || name.trim().length() > 200)
            throw new BusinessException(400, "名称不能为空或超过长度限制");
        po.setName(name.trim());
        String categoryId = po.getCategoryId();
        if (categoryId == null || categoryId.isBlank()) {
            po.setCategoryId(null);
        } else if (categoryId.length() > 32) {
            throw new BusinessException(400, "资源ID无效");
        }
        requireCategory(owner, po.getThemeId(), po.getCategoryId());
        if (po.getNote() != null && po.getNote().length() > MAX_NOTE_LENGTH)
            throw new BusinessException(400, "笔记最多10000字");
    }

    private void validateQuery(String owner, MapPointQueryDto query) {
        if (query.getThemeId() == null || query.getThemeId().isBlank() || query.getThemeId().length() > 32) throw new BusinessException(400, "资源ID无效");
        MapTheme theme = themeMapper.selectById(owner, query.getThemeId());
        if (theme == null) throw new BusinessException(404, "资源不存在");
        if (!Integer.valueOf(ACTIVE).equals(theme.getStatus())) throw new BusinessException(404, "资源不存在");
        String categoryId = query.getCategoryId();
        if (categoryId == null || categoryId.isBlank()) {
            query.setCategoryId(null);
        } else if (categoryId.length() > 32) {
            throw new BusinessException(400, "资源ID无效");
        }
        if (Boolean.TRUE.equals(query.getUncategorized()) && query.getCategoryId() != null)
            throw new BusinessException(400, "分类与未分类筛选不能同时指定");
        requireCategory(owner, query.getThemeId(), query.getCategoryId());
        String keyword = query.getKeyword();
        if (keyword == null || keyword.isEmpty()) {
            query.setKeyword(null);
        } else {
            if (keyword.length() > 200) throw new BusinessException(400, "搜索词最多200字");
            query.setKeyword(keyword.replace("!", "!!").replace("%", "!%").replace("_", "!_"));
        }
    }
}
