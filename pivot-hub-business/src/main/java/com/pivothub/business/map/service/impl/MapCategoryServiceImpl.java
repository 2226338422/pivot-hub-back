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
public class MapCategoryServiceImpl implements MapCategoryService {
    @Autowired
    private MapCategoryMapper categoryMapper;
    @Autowired
    private MapThemeMapper themeMapper;
    @Autowired
    private MapPointMapper pointMapper;

    @Override
    public List<MapCategoryVo> list(String themeId) {
        String owner = TLUtil.get(TLUtil.USER_ID);
        if (owner == null || owner.isBlank()) throw new BusinessException(500, "用户身份上下文不可用");
        if (themeId == null || themeId.isBlank() || themeId.length() > 32) throw new BusinessException(400, "资源ID无效");
        MapTheme theme = themeMapper.selectById(owner, themeId);
        if (theme == null) throw new BusinessException(404, "资源不存在");
        if (!Integer.valueOf(ACTIVE).equals(theme.getStatus())) throw new BusinessException(404, "资源不存在");
        return categoryMapper.selectList(owner, themeId).stream().map(po -> {
            MapCategoryVo vo = new MapCategoryVo();
            BeanUtils.copyProperties(po, vo);
            return vo;
        }).toList();
    }

    @Override
    @Transactional(rollbackFor = Exception.class, isolation = Isolation.READ_COMMITTED)
    public MapCategoryVo add(MapCategoryCreateDto request) {
        String owner = TLUtil.get(TLUtil.USER_ID);
        if (owner == null || owner.isBlank()) throw new BusinessException(500, "用户身份上下文不可用");
        if (request.getThemeId() == null || request.getThemeId().isBlank() || request.getThemeId().length() > 32) throw new BusinessException(400, "资源ID无效");
        lockTheme(owner, request.getThemeId());
        MapCategory po = new MapCategory();
        BeanUtils.copyProperties(request, po);
        po.setUuid(IdWorker.getIdStr());
        po.setOwnerUserId(owner);
        po.setStatus(ACTIVE);
        validate(owner, po, null);
        po.setCreateUserId(owner);
        po.setUpdateUserId(owner);
        if (categoryMapper.insert(po) != 1) throw new BusinessException(409, "资源已变更，请重新加载");
        MapCategory saved = categoryMapper.selectById(owner, po.getUuid());
        if (saved == null) throw new BusinessException(404, "资源不存在");
        MapCategoryVo vo = new MapCategoryVo();
        BeanUtils.copyProperties(saved, vo);
        return vo;
    }

    @Override
    @Transactional(rollbackFor = Exception.class, isolation = Isolation.READ_COMMITTED)
    public MapCategoryVo update(MapCategoryUpdateDto request) {
        String owner = TLUtil.get(TLUtil.USER_ID);
        if (owner == null || owner.isBlank()) throw new BusinessException(500, "用户身份上下文不可用");
        if (request.getCategoryId() == null || request.getCategoryId().isBlank() || request.getCategoryId().length() > 32) throw new BusinessException(400, "资源ID无效");
        MapCategory original = categoryMapper.selectById(owner, request.getCategoryId());
        if (original == null) throw new BusinessException(404, "资源不存在");
        if (!Integer.valueOf(ACTIVE).equals(original.getStatus())) throw new BusinessException(404, "资源不存在");
        lockTheme(owner, original.getThemeId());
        MapCategory po = categoryMapper.selectByIdForUpdate(owner, original.getUuid());
        if (po == null) throw new BusinessException(404, "资源不存在");
        if (!Integer.valueOf(ACTIVE).equals(po.getStatus())) throw new BusinessException(404, "资源不存在");
        BeanUtils.copyProperties(request, po);
        validate(owner, po, po.getUuid());
        po.setUpdateUserId(owner);
        if (categoryMapper.update(po) != 1) throw new BusinessException(409, "资源已变更，请重新加载");
        MapCategory saved = categoryMapper.selectById(owner, po.getUuid());
        if (saved == null) throw new BusinessException(404, "资源不存在");
        MapCategoryVo vo = new MapCategoryVo();
        BeanUtils.copyProperties(saved, vo);
        return vo;
    }

    @Override
    @Transactional(rollbackFor = Exception.class, isolation = Isolation.READ_COMMITTED)
    public void delete(String categoryId) {
        String owner = TLUtil.get(TLUtil.USER_ID);
        if (owner == null || owner.isBlank()) throw new BusinessException(500, "用户身份上下文不可用");
        if (categoryId == null || categoryId.isBlank() || categoryId.length() > 32) throw new BusinessException(400, "资源ID无效");
        MapCategory original = categoryMapper.selectById(owner, categoryId);
        if (original == null) throw new BusinessException(404, "资源不存在");
        MapTheme theme = themeMapper.selectByIdForUpdate(owner, original.getThemeId());
        if (theme == null) throw new BusinessException(404, "资源不存在");
        MapCategory po = categoryMapper.selectByIdForUpdate(owner, categoryId);
        if (po == null) throw new BusinessException(404, "资源不存在");
        if (Integer.valueOf(DELETED).equals(po.getStatus())) return;
        if (!Integer.valueOf(ACTIVE).equals(theme.getStatus())) throw new BusinessException(404, "资源不存在");
        if (!Integer.valueOf(ACTIVE).equals(po.getStatus())) throw new BusinessException(404, "资源不存在");
        if (pointMapper.countCategoryReferences(owner, po.getThemeId(), categoryId) > 0)
            throw new BusinessException(409, "分类仍被地点引用，请先调整地点分类");
        if (categoryMapper.markDeleted(owner, categoryId) != 1) throw new BusinessException(409, "资源已变更，请重新加载");
    }

    private void lockTheme(String owner, String themeId) {
        MapTheme theme = themeMapper.selectByIdForUpdate(owner, themeId);
        if (theme == null) throw new BusinessException(404, "资源不存在");
        if (!Integer.valueOf(ACTIVE).equals(theme.getStatus())) throw new BusinessException(404, "资源不存在");
    }

    private void validate(String owner, MapCategory po, String excludeId) {
        String name = po.getName();
        if (name == null || name.trim().isEmpty() || name.trim().length() > 64)
            throw new BusinessException(400, "名称不能为空或超过长度限制");
        po.setName(name.trim());
        String iconId = po.getIconId();
        if (iconId == null || iconId.isBlank()) {
            po.setIconId(null);
        } else if (iconId.length() > 32) {
            throw new BusinessException(400, "资源ID无效");
        }
        if (categoryMapper.countName(owner, po.getThemeId(), po.getName(), excludeId) > 0)
            throw new BusinessException(409, "该主题下分类名称已存在");
        if (po.getIconId() != null) {
            throw new BusinessException(400, "分类图标绑定暂未开放，待统一文件服务接入");
        }
    }
}
