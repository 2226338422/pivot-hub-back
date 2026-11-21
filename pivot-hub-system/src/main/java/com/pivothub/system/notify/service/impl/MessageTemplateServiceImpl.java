package com.pivothub.system.notify.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.pivothub.common.exception.SystemException;
import com.pivothub.commoncore.enums.NotifyMessageType;
import com.pivothub.pojo.dto.system.notify.MessageTemplatePageDto;
import com.pivothub.pojo.dto.system.notify.MessageTemplateSaveDto;
import com.pivothub.pojo.po.system.SysMessageTemplate;
import com.pivothub.pojo.vo.PageVo;
import com.pivothub.pojo.vo.system.notify.MessageTemplateManageVo;
import com.pivothub.pojo.vo.system.notify.MessageTemplateVo;
import com.pivothub.system.notify.mapper.SysMessageTemplateMapper;
import com.pivothub.system.notify.service.MessageTemplateService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;

/** 查询和维护消息模板。 */
@Service
public class MessageTemplateServiceImpl implements MessageTemplateService {
    @Autowired
    private SysMessageTemplateMapper sysMessageTemplateMapper;

    @Override
    public MessageTemplateVo find(String messageType) {
        if (!NotifyMessageType.supports(messageType)) {
            throw new SystemException(400, "不支持的消息类型");
        }
        SysMessageTemplate template = sysMessageTemplateMapper.selectByType(messageType);
        if (template == null) {
            throw new SystemException(404, "消息模板不存在");
        }

        MessageTemplateVo result = new MessageTemplateVo();
        result.setBodyTemplate(template.getBodyTemplate());
        result.setItemTemplate(template.getItemTemplate());
        return result;
    }

    @Override
    public PageVo<MessageTemplateManageVo> page(MessageTemplatePageDto query) {
        Integer pageNum = query.getPageNum();
        Integer pageSize = query.getPageSize();
        if (pageNum == null || pageNum < 1 || pageSize == null || pageSize < 1 || pageSize > 100) {
            throw new SystemException(400, "页码至少为1，每页条数为1到100");
        }
        List<SysMessageTemplate> rows;
        PageHelper.startPage(pageNum, pageSize);
        try {
            rows = sysMessageTemplateMapper.selectPage(query);
        } finally {
            PageHelper.clearPage();
        }
        long total = new PageInfo<>(rows).getTotal();
        return PageVo.of(pageNum, pageSize, total, rows.stream().map(this::toManageVo).toList());
    }

    @Override
    public MessageTemplateManageVo detail(String uuid) {
        if (!StringUtils.hasText(uuid) || uuid.length() > 32) {
            throw new SystemException(400, "消息模板 UUID 无效");
        }
        SysMessageTemplate template = sysMessageTemplateMapper.selectByUuid(uuid);
        if (template == null) {
            throw new SystemException(404, "消息模板不存在");
        }
        return toManageVo(template);
    }

    @Override
    public void update(MessageTemplateSaveDto request) {
        if (!StringUtils.hasText(request.getItemTemplate())) {
            request.setItemTemplate(null);
        }
        if (sysMessageTemplateMapper.updateContent(request) == 0
                && !sysMessageTemplateMapper.existsByUuid(request.getUuid())) {
            throw new SystemException(404, "消息模板不存在");
        }
    }

    /** 组装管理页使用的模板信息和消息类型名称。 */
    private MessageTemplateManageVo toManageVo(SysMessageTemplate template) {
        NotifyMessageType type = NotifyMessageType.findByValue(template.getMessageType());
        MessageTemplateManageVo result = new MessageTemplateManageVo();
        BeanUtils.copyProperties(template, result);
        result.setMessageTypeName(type == null ? template.getMessageType() : type.getName());
        return result;
    }
}
