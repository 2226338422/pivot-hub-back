package com.pivothub.system.announcement.service.impl;

import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.pivothub.common.exception.SystemException;
import com.pivothub.common.notify.template.MessageTemplateRenderer;
import com.pivothub.commoncore.enums.FileType;
import com.pivothub.commoncore.enums.NotifyMessageType;
import com.pivothub.pojo.dto.system.announcement.AnnouncementPageDto;
import com.pivothub.pojo.dto.system.announcement.AnnouncementFeedQueryDto;
import com.pivothub.pojo.dto.system.announcement.AnnouncementSaveDto;
import com.pivothub.pojo.dto.system.notify.NotifyEventDto;
import com.pivothub.pojo.po.system.SysAnnouncement;
import com.pivothub.pojo.po.system.SysAnnouncementFile;
import com.pivothub.pojo.po.system.SysFile;
import com.pivothub.pojo.vo.PageVo;
import com.pivothub.pojo.vo.system.announcement.AnnouncementVo;
import com.pivothub.pojo.vo.system.announcement.AnnouncementFeedVo;
import com.pivothub.pojo.vo.system.announcement.AnnouncementFeedItemVo;
import com.pivothub.pojo.vo.system.announcement.AnnouncementLatestVo;
import com.pivothub.system.announcement.mapper.SysAnnouncementFileMapper;
import com.pivothub.system.announcement.mapper.SysAnnouncementMapper;
import com.pivothub.system.announcement.service.AnnouncementService;
import com.pivothub.system.mapper.SysFileMapper;
import com.pivothub.system.notify.service.MessageTemplateService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Service
public class AnnouncementServiceImpl implements AnnouncementService {
    private static final Logger LOG = LoggerFactory.getLogger(AnnouncementServiceImpl.class);
    private static final long MAX_ANNOUNCEMENT_IMAGE_BYTES = 5L * 1024 * 1024;

    @Autowired
    private SysAnnouncementMapper sysAnnouncementMapper;

    @Autowired
    private SysAnnouncementFileMapper sysAnnouncementFileMapper;

    @Autowired
    private SysFileMapper sysFileMapper;

    @Autowired
    private MessageTemplateService messageTemplateService;

    @Autowired
    private ApplicationEventPublisher applicationEventPublisher;

    @Override
    public AnnouncementFeedVo feedPublished(AnnouncementFeedQueryDto query) {
        query.setTitle(StringUtils.hasText(query.getTitle()) ? query.getTitle().trim() : null);
        if (query.getPublishTimeOrder() == null) query.setPublishTimeOrder(0);
        if (query.getCursorId() != null && !query.getCursorId().matches("[A-Za-z0-9_-]{1,32}")) {
            throw new SystemException(400, "公告游标UUID无效");
        }
        List<AnnouncementVo> fetched = sysAnnouncementMapper.selectFeed(query, query.getLimit() + 1);
        boolean hasMore = fetched.size() > query.getLimit();
        List<AnnouncementVo> rows = hasMore ? fetched.subList(0, query.getLimit()) : fetched;
        AnnouncementFeedVo feed = new AnnouncementFeedVo();
        feed.setList(toVosWithFiles(rows).stream().map(announcement -> {
            AnnouncementFeedItemVo item = new AnnouncementFeedItemVo();
            BeanUtils.copyProperties(announcement, item);
            return item;
        }).toList());
        feed.setHasMore(hasMore);
        if (hasMore) {
            feed.setNextCursorId(rows.get(rows.size() - 1).getUuid());
        }
        return feed;
    }

    @Override
    public AnnouncementLatestVo latestPublished() {
        return sysAnnouncementMapper.selectLatestPublished();
    }

    @Override
    public PageVo<AnnouncementVo> pageManage(AnnouncementPageDto query) {
        query.setTitle(StringUtils.hasText(query.getTitle()) ? query.getTitle().trim() : null);
        if (query.getPublishTimeOrder() == null) query.setPublishTimeOrder(0);
        List<AnnouncementVo> rows;
        PageHelper.startPage(query.getPageNum(), query.getPageSize());
        try {
            rows = sysAnnouncementMapper.selectPage(query);
        } finally {
            PageHelper.clearPage();
        }
        long total = new PageInfo<>(rows).getTotal();
        return PageVo.of(query.getPageNum(), query.getPageSize(), total, toVosWithFiles(rows));
    }

    @Override
    public AnnouncementVo detailManage(String id) {
        AnnouncementVo announcement = sysAnnouncementMapper.selectById(id);
        if (announcement == null) throw new SystemException(404, "公告不存在");
        announcement.setFileIds(sysAnnouncementFileMapper.selectByAnnouncementIds(List.of(id))
                .stream().map(SysAnnouncementFile::getFileId).toList());
        return announcement;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public AnnouncementVo create(String operatorId, AnnouncementSaveDto request) {
        List<String> fileIds = validateFileIds(request.getFileIds(), operatorId, Set.of());
        SysAnnouncement announcement = new SysAnnouncement();
        announcement.setUuid(IdWorker.getIdStr());
        announcement.setTitle(request.getTitle().trim());
        announcement.setContent(request.getContent().trim());
        announcement.setStatus(0);
        announcement.setCreateUserId(operatorId);
        announcement.setUpdateUserId(operatorId);
        if (sysAnnouncementMapper.insert(announcement) != 1) throw new SystemException(500, "创建公告失败");
        replaceFiles(announcement.getUuid(), fileIds, operatorId);
        AnnouncementVo result = sysAnnouncementMapper.selectById(announcement.getUuid());
        if (result == null) throw new SystemException(404, "公告不存在");
        result.setFileIds(fileIds);
        return result;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void update(String operatorId, String id, AnnouncementSaveDto request) {
        SysAnnouncement announcement = sysAnnouncementMapper.selectByIdForUpdate(id);
        if (announcement == null) throw new SystemException(404, "公告不存在");
        if (!Integer.valueOf(0).equals(announcement.getStatus())) {
            throw new SystemException(409, "只有草稿可以编辑");
        }
        List<String> oldFileIds = sysAnnouncementFileMapper.selectByAnnouncementIds(List.of(id))
                .stream().map(SysAnnouncementFile::getFileId).toList();
        List<String> newFileIds = validateFileIds(request.getFileIds(), operatorId, new HashSet<>(oldFileIds));
        if (sysAnnouncementMapper.updateDraft(id, request.getTitle().trim(), request.getContent().trim(), operatorId) != 1) {
            throw new SystemException(409, "公告状态已变化，请刷新后重试");
        }
        if (!oldFileIds.equals(newFileIds)) {
            replaceFiles(id, newFileIds, operatorId);
            if (sysAnnouncementMapper.touchDraft(id, operatorId) != 1) {
                throw new SystemException(409, "公告状态已变化，请刷新后重试");
            }
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void publish(String operatorId, String id) {
        SysAnnouncement announcement = sysAnnouncementMapper.selectByIdForUpdate(id);
        if (announcement == null) throw new SystemException(404, "公告不存在");
        if (!Integer.valueOf(0).equals(announcement.getStatus())) {
            throw new SystemException(409, "只有草稿可以发布");
        }
        if (!StringUtils.hasText(announcement.getTitle()) || !StringUtils.hasText(announcement.getContent())) {
            throw new SystemException(400, "公告标题和正文不能为空");
        }
        if (sysAnnouncementMapper.publish(id, operatorId) != 1) {
            throw new SystemException(409, "公告状态已变化，请刷新后重试");
        }
        publishAnnouncementEvent(announcement);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void withdraw(String operatorId, String id) {
        SysAnnouncement announcement = sysAnnouncementMapper.selectByIdForUpdate(id);
        if (announcement == null) throw new SystemException(404, "公告不存在");
        if (!Integer.valueOf(1).equals(announcement.getStatus())) {
            throw new SystemException(409, "只有已发布公告可以撤回");
        }
        if (sysAnnouncementMapper.withdraw(id, operatorId) != 1) {
            throw new SystemException(409, "公告状态已变化，请刷新后重试");
        }
    }

    /** 生成通知内容并发布公告事件。 */
    private void publishAnnouncementEvent(SysAnnouncement announcement) {
        String announcementId = announcement.getUuid();
        NotifyEventDto event = new NotifyEventDto();
        try {
            String messageType = NotifyMessageType.ANNOUNCEMENT.getValue();
            String template = messageTemplateService.find(messageType).getBodyTemplate();
            String body = MessageTemplateRenderer.render(template, announcement.getContent());
            event.setMessageType(messageType);
            event.setSourceId(announcementId);
            event.setTitle(announcement.getTitle());
            event.setBody(body);
        } catch (RuntimeException failure) {
            LOG.warn("准备公告通知失败 announcementId={}", announcementId, failure);
            return;
        }

        applicationEventPublisher.publishEvent(event);
    }

    /** 批量查询公告图片关联并写入展示对象。 */
    private List<AnnouncementVo> toVosWithFiles(List<AnnouncementVo> rows) {
        Map<String, List<String>> filesByAnnouncement = new HashMap<>();
        if (!rows.isEmpty()) {
            List<String> ids = rows.stream().map(AnnouncementVo::getUuid).toList();
            for (SysAnnouncementFile file : sysAnnouncementFileMapper.selectByAnnouncementIds(ids)) {
                filesByAnnouncement.computeIfAbsent(file.getAnnouncementId(), ignored -> new ArrayList<>())
                        .add(file.getFileId());
            }
        }
        return rows.stream().map(row -> {
            row.setFileIds(filesByAnnouncement.getOrDefault(row.getUuid(), List.of()));
            return row;
        }).toList();
    }

    /** 校验附件去重、业务类型、文件大小和使用权限。 */
    private List<String> validateFileIds(List<String> submitted, String operatorId, Set<String> existingIds) {
        if (submitted == null) return List.of();
        List<String> ids = new ArrayList<>(submitted.size());
        Set<String> seen = new HashSet<>();
        for (String id : submitted) {
            if (!StringUtils.hasText(id) || id.trim().length() > 32 || !seen.add(id.trim())) {
                throw new SystemException(400, "公告附件ID不能为空或重复");
            }
            ids.add(id.trim());
        }
        if (ids.isEmpty()) return ids;
        Map<String, SysFile> files = new HashMap<>();
        for (SysFile file : sysFileMapper.selectByUuids(ids)) files.put(file.getUuid(), file);
        for (String id : ids) {
            SysFile file = files.get(id);
            if (file == null || !FileType.ANNOUNCEMENT_FILE.getModule().equals(file.getModule())
                    || !FileType.ANNOUNCEMENT_FILE.getValue().equals(file.getType())
                    || (file.getFileSize() != null && file.getFileSize() > MAX_ANNOUNCEMENT_IMAGE_BYTES)
                    || (!existingIds.contains(id) && !operatorId.equals(file.getUploaderId()))) {
                throw new SystemException(400, "公告图片无效或无权使用");
            }
        }
        return ids;
    }

    /** 按提交顺序替换公告与附件的关联记录。 */
    private void replaceFiles(String announcementId, List<String> fileIds, String operatorId) {
        sysAnnouncementFileMapper.deleteByAnnouncementId(announcementId);
        if (fileIds.isEmpty()) return;
        List<SysAnnouncementFile> relations = new ArrayList<>(fileIds.size());
        for (int index = 0; index < fileIds.size(); index++) {
            SysAnnouncementFile relation = new SysAnnouncementFile();
            relation.setUuid(IdWorker.getIdStr());
            relation.setAnnouncementId(announcementId);
            relation.setFileId(fileIds.get(index));
            relation.setSort(index);
            relation.setCreateUserId(operatorId);
            relation.setUpdateUserId(operatorId);
            relations.add(relation);
        }
        if (sysAnnouncementFileMapper.insertBatch(relations) != relations.size()) {
            throw new SystemException(500, "保存公告图片失败");
        }
    }
}
