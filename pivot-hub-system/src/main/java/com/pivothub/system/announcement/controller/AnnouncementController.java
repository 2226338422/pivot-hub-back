package com.pivothub.system.announcement.controller;

import com.pivothub.common.result.Result;
import com.pivothub.pojo.dto.system.announcement.AnnouncementFeedQueryDto;
import com.pivothub.pojo.vo.system.announcement.AnnouncementFeedVo;
import com.pivothub.pojo.vo.system.announcement.AnnouncementLatestVo;
import com.pivothub.system.annotation.MenuPermission;
import com.pivothub.system.announcement.service.AnnouncementService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/system/announcement")
@MenuPermission("notice_list")
public class AnnouncementController {
    @Autowired
    private AnnouncementService announcementService;

    /** 按游标查询已发布公告列表。 */
    @GetMapping("/feed")
    public Result<AnnouncementFeedVo> feed(@Valid AnnouncementFeedQueryDto query) {
        return Result.success(announcementService.feedPublished(query));
    }

    /** 查询最近发布公告的标题和发布时间。 */
    @GetMapping("/latest")
    public Result<AnnouncementLatestVo> latest() {
        return Result.success(announcementService.latestPublished());
    }

}
