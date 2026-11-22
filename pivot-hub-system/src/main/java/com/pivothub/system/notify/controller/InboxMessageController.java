package com.pivothub.system.notify.controller;

import com.pivothub.common.result.Result;
import com.pivothub.pojo.dto.system.notify.InboxMessagePageDto;
import com.pivothub.pojo.dto.system.notify.InboxMessageReadDto;
import com.pivothub.pojo.vo.PageVo;
import com.pivothub.pojo.vo.system.notify.InboxMessageVo;
import com.pivothub.system.annotation.MenuPermission;
import com.pivothub.system.notify.service.InboxMessageService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/** 当前用户的站内信查询及已读操作。 */
@RestController
@RequestMapping("/system/notify/inbox")
@MenuPermission("inbox")
public class InboxMessageController {
    @Autowired
    private InboxMessageService inboxMessageService;

    /** 分页查询当前用户收到的消息。 */
    @Operation(summary = "分页查询收到的消息")
    @GetMapping
    public Result<PageVo<InboxMessageVo>> page(InboxMessagePageDto query) {
        return Result.success(inboxMessageService.page(query));
    }

    /** 按 UUID 查看当前用户收到的消息。 */
    @Operation(summary = "查询消息详情")
    @GetMapping("/{uuid}")
    public Result<InboxMessageVo> detail(@PathVariable("uuid") String uuid) {
        return Result.success(inboxMessageService.detail(uuid));
    }

    /** 将指定站内信设为已读，返回更新条数。 */
    @Operation(summary = "将指定站内信设为已读")
    @PutMapping("/read")
    public Result<Integer> markRead(@Valid @RequestBody InboxMessageReadDto request) {
        return Result.success(inboxMessageService.markRead(request.getUuids()));
    }

    /** 将当前用户的全部未读站内信设为已读，返回更新条数。 */
    @Operation(summary = "将全部站内信设为已读")
    @PutMapping("/read-all")
    public Result<Integer> markAllRead() {
        return Result.success(inboxMessageService.markAllRead());
    }
}
