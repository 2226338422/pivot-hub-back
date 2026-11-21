package com.pivothub.system.notify.service;

import com.pivothub.pojo.dto.system.notify.MessageTemplatePageDto;
import com.pivothub.pojo.dto.system.notify.MessageTemplateSaveDto;
import com.pivothub.pojo.vo.PageVo;
import com.pivothub.pojo.vo.system.notify.MessageTemplateManageVo;
import com.pivothub.pojo.vo.system.notify.MessageTemplateVo;

/** 系统消息模板查询和管理服务。 */
public interface MessageTemplateService {
    /** 读取指定消息类型的模板。 */
    MessageTemplateVo find(String messageType);

    /** 分页查询管理页的消息模板。 */
    PageVo<MessageTemplateManageVo> page(MessageTemplatePageDto query);

    /** 按 UUID 查询模板详情。 */
    MessageTemplateManageVo detail(String uuid);

    /** 修改指定模板的正文和明细模板。 */
    void update(MessageTemplateSaveDto request);
}
