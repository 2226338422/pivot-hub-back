package com.pivothub.business.map.service.impl;

import org.springframework.beans.BeanUtils;
import com.pivothub.common.util.TLUtil;
import org.springframework.beans.factory.annotation.Autowired;
import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.pivothub.business.map.mapper.*;
import com.pivothub.business.map.service.*;
import com.pivothub.common.exception.BusinessException;
import com.pivothub.pojo.dto.business.map.*;
import com.pivothub.pojo.po.business.map.*;
import com.pivothub.pojo.vo.business.map.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.annotation.Isolation;
import java.util.List;
import static com.pivothub.commoncore.constants.map.MapConstants.*;

@Service
public class MapThemeServiceImpl implements MapThemeService {
    @Autowired
    private MapThemeMapper themeMapper;
    @Autowired
    private MapCategoryMapper categoryMapper;
    @Autowired
    private MapPointMapper pointMapper;

    @Override
    public List<MapThemeVo> list() {
        String owner = TLUtil.get(TLUtil.USER_ID);
        if (owner == null || owner.isBlank()) throw new BusinessException(500, "用户身份上下文不可用");
        return themeMapper.selectList(owner).stream().map(po -> {
            MapThemeVo vo = new MapThemeVo();
            BeanUtils.copyProperties(po, vo);
            return vo;
        }).toList();
    }

    @Override
    public MapThemeVo detail(String themeId) {
        String owner = TLUtil.get(TLUtil.USER_ID);
        if (owner == null || owner.isBlank()) throw new BusinessException(500, "用户身份上下文不可用");
        if (themeId == null || themeId.isBlank() || themeId.length() > 32) throw new BusinessException(400, "资源ID无效");
        MapTheme po = themeMapper.selectById(owner, themeId);
        if (po == null) throw new BusinessException(404, "资源不存在");
        if (!Integer.valueOf(ACTIVE).equals(po.getStatus())) throw new BusinessException(404, "资源不存在");
        MapThemeVo vo = new MapThemeVo();
        BeanUtils.copyProperties(po, vo);
        return vo;
    }

    @Override
    @Transactional(rollbackFor = Exception.class, isolation = Isolation.READ_COMMITTED)
    public MapThemeVo add(MapThemeCreateDto request) {
        String owner = TLUtil.get(TLUtil.USER_ID);
        if (owner == null || owner.isBlank()) throw new BusinessException(500, "用户身份上下文不可用");
        MapTheme po = new MapTheme();
        BeanUtils.copyProperties(request, po);
        validate(po);
        po.setUuid(IdWorker.getIdStr());
        po.setOwnerUserId(owner);
        po.setStatus(ACTIVE);
        po.setCreateUserId(owner);
        po.setUpdateUserId(owner);
        if (themeMapper.insert(po) != 1) throw new BusinessException(409, "资源已变更，请重新加载");
        MapTheme saved = themeMapper.selectById(owner, po.getUuid());
        if (saved == null) throw new BusinessException(404, "资源不存在");
        MapThemeVo vo = new MapThemeVo();
        BeanUtils.copyProperties(saved, vo);
        return vo;
    }

    @Override
    @Transactional(rollbackFor = Exception.class, isolation = Isolation.READ_COMMITTED)
    public MapThemeVo update(MapThemeUpdateDto request) {
        String owner = TLUtil.get(TLUtil.USER_ID);
        if (owner == null || owner.isBlank()) throw new BusinessException(500, "用户身份上下文不可用");
        if (request.getThemeId() == null || request.getThemeId().isBlank() || request.getThemeId().length() > 32) throw new BusinessException(400, "资源ID无效");
        MapTheme po = themeMapper.selectByIdForUpdate(owner, request.getThemeId());
        if (po == null) throw new BusinessException(404, "资源不存在");
        if (!Integer.valueOf(ACTIVE).equals(po.getStatus())) throw new BusinessException(404, "资源不存在");
        BeanUtils.copyProperties(request, po);
        validate(po);
        po.setUpdateUserId(owner);
        if (themeMapper.update(po) != 1) throw new BusinessException(409, "资源已变更，请重新加载");
        MapTheme saved = themeMapper.selectById(owner, po.getUuid());
        if (saved == null) throw new BusinessException(404, "资源不存在");
        MapThemeVo vo = new MapThemeVo();
        BeanUtils.copyProperties(saved, vo);
        return vo;
    }

    @Override
    @Transactional(rollbackFor = Exception.class, isolation = Isolation.READ_COMMITTED)
    public void delete(String themeId) {
        String owner = TLUtil.get(TLUtil.USER_ID);
        if (owner == null || owner.isBlank()) throw new BusinessException(500, "用户身份上下文不可用");
        if (themeId == null || themeId.isBlank() || themeId.length() > 32) throw new BusinessException(400, "资源ID无效");
        MapTheme po = themeMapper.selectByIdForUpdate(owner, themeId);
        if (po == null) throw new BusinessException(404, "资源不存在");
        if (Integer.valueOf(DELETED).equals(po.getStatus())) return;
        if (!Integer.valueOf(ACTIVE).equals(po.getStatus())) throw new BusinessException(404, "资源不存在");
        pointMapper.deleteByTheme(owner, themeId);
        categoryMapper.deleteByTheme(owner, themeId);
        if (themeMapper.markDeleted(owner, themeId) != 1) throw new BusinessException(409, "资源已变更，请重新加载");
    }

    private void validate(MapTheme po) {
        String name = po.getName();
        if (name == null || name.trim().isEmpty() || name.trim().length() > 100)
            throw new BusinessException(400, "名称不能为空或超过长度限制");
        po.setName(name.trim());
        if ((po.getCenterLongitude() == null) != (po.getCenterLatitude() == null))
            throw new BusinessException(400, "中心经纬度必须同时设置或同时为空");
    }
}
