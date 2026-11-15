package com.pivothub.system.announcement.service;

import com.pivothub.pojo.dto.system.announcement.AnnouncementPageDto;
import com.pivothub.pojo.dto.system.announcement.AnnouncementFeedQueryDto;
import com.pivothub.pojo.dto.system.announcement.AnnouncementSaveDto;
import com.pivothub.pojo.vo.PageVo;
import com.pivothub.pojo.vo.system.announcement.AnnouncementVo;
import com.pivothub.pojo.vo.system.announcement.AnnouncementFeedVo;
import com.pivothub.pojo.vo.system.announcement.AnnouncementLatestVo;

public interface AnnouncementService {
    /** 按游标查询已发布公告内容流。 */
    AnnouncementFeedVo feedPublished(AnnouncementFeedQueryDto query);

    /** 查询最近发布公告的标题与发布时间。 */
    AnnouncementLatestVo latestPublished();

    /** 分页查询管理端公告，支持状态筛选。 */
    PageVo<AnnouncementVo> pageManage(AnnouncementPageDto query);

    /** 查询管理端公告详情，包括草稿和已撤回公告。 */
    AnnouncementVo detailManage(String id);

    /** 创建公告草稿，操作人标识由认证上下文取得。 */
    AnnouncementVo create(String operatorId, AnnouncementSaveDto request);

    /** 编辑公告草稿。 */
    void update(String operatorId, String id, AnnouncementSaveDto request);

    /** 将草稿发布为公告。 */
    void publish(String operatorId, String id);

    /** 撤回已发布公告。 */
    void withdraw(String operatorId, String id);
}
