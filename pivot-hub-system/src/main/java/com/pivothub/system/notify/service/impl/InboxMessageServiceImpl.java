package com.pivothub.system.notify.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.pivothub.common.exception.SystemException;
import com.pivothub.common.util.TLUtil;
import com.pivothub.commoncore.enums.NotifyChannel;
import com.pivothub.pojo.dto.system.notify.InboxMessagePageDto;
import com.pivothub.pojo.vo.PageVo;
import com.pivothub.pojo.vo.system.notify.InboxMessageVo;
import com.pivothub.system.notify.mapper.SysUserMessageMapper;
import com.pivothub.system.notify.service.InboxMessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class InboxMessageServiceImpl implements InboxMessageService {
    @Autowired
    private SysUserMessageMapper sysUserMessageMapper;

    @Override
    public PageVo<InboxMessageVo> page(InboxMessagePageDto query) {
        Integer pageNum = query.getPageNum();
        Integer pageSize = query.getPageSize();
        if (pageNum == null || pageNum < 1 || pageSize == null || pageSize < 1 || pageSize > 100) {
            throw new SystemException(400, "页码至少为1，每页条数为1到100");
        }
        if (query.getCreateTimeStart() != null && query.getCreateTimeEnd() != null
                && query.getCreateTimeStart().isAfter(query.getCreateTimeEnd())) {
            throw new SystemException(400, "创建时间起点不能晚于终点");
        }

        String userId = TLUtil.get(TLUtil.USER_ID);
        List<InboxMessageVo> messages;
        PageHelper.startPage(pageNum, pageSize);
        try {
            messages = sysUserMessageMapper.selectInboxPage(userId, query, NotifyChannel.INBOX.getValue());
        } finally {
            PageHelper.clearPage();
        }
        return PageVo.of(pageNum, pageSize, new PageInfo<>(messages).getTotal(), messages);
    }

    @Override
    public InboxMessageVo detail(String uuid) {
        String userId = TLUtil.get(TLUtil.USER_ID);
        InboxMessageVo message = sysUserMessageMapper.selectInboxDetail(uuid, userId, NotifyChannel.INBOX.getValue());
        if (message == null) {
            throw new SystemException(404, "消息不存在");
        }
        return message;
    }

    @Override
    public int markRead(List<String> uuids) {
        String userId = TLUtil.get(TLUtil.USER_ID);
        return sysUserMessageMapper.markInboxRead(userId, NotifyChannel.INBOX.getValue(), uuids);
    }

    @Override
    public int markAllRead() {
        String userId = TLUtil.get(TLUtil.USER_ID);
        return sysUserMessageMapper.markAllInboxRead(userId, NotifyChannel.INBOX.getValue());
    }
}
