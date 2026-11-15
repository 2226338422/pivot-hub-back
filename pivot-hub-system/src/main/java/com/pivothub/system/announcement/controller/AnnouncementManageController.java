package com.pivothub.system.announcement.controller;

import com.pivothub.common.result.Result;
import com.pivothub.common.util.TLUtil;
import com.pivothub.pojo.dto.system.announcement.AnnouncementPageDto;
import com.pivothub.pojo.dto.system.announcement.AnnouncementSaveDto;
import com.pivothub.pojo.vo.PageVo;
import com.pivothub.pojo.vo.system.announcement.AnnouncementVo;
import com.pivothub.system.annotation.MenuPermission;
import com.pivothub.system.announcement.service.AnnouncementService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/system/announcement/manage")
@MenuPermission("notice_manage")
public class AnnouncementManageController {
    @Autowired
    private AnnouncementService announcementService;

    /** 分页查询全部状态的公告。 */
    @GetMapping("/page")
    public Result<PageVo<AnnouncementVo>> page(@Valid AnnouncementPageDto query) {
        return Result.success(announcementService.pageManage(query));
    }

    /** 查询公告管理详情。 */
    @GetMapping("/detail")
    public Result<AnnouncementVo> detail(@RequestParam(value = "id", required = true) String id) {
        return Result.success(announcementService.detailManage(id));
    }

    /** 创建公告草稿。 */
    @PostMapping("/create")
    public Result<AnnouncementVo> create(@Valid @RequestBody AnnouncementSaveDto request) {
        return Result.success(announcementService.create(TLUtil.get(TLUtil.USER_ID), request));
    }

    /** 编辑公告草稿。 */
    @PutMapping("/update")
    public Result<Object> update(@RequestParam(value = "id", required = true) String id,
                                 @Valid @RequestBody AnnouncementSaveDto request) {
        announcementService.update(TLUtil.get(TLUtil.USER_ID), id, request);
        return Result.success(null);
    }

    /** 发布公告草稿。 */
    @PostMapping("/publish")
    public Result<Object> publish(@RequestParam(value = "id", required = true) String id) {
        announcementService.publish(TLUtil.get(TLUtil.USER_ID), id);
        return Result.success(null);
    }

    /** 撤回已发布公告。 */
    @PostMapping("/withdraw")
    public Result<Object> withdraw(@RequestParam(value = "id", required = true) String id) {
        announcementService.withdraw(TLUtil.get(TLUtil.USER_ID), id);
        return Result.success(null);
    }
}
