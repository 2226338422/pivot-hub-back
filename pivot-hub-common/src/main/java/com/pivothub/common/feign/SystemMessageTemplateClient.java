package com.pivothub.common.feign;

import com.pivothub.pojo.dto.system.notify.MessageTemplateQueryDto;
import com.pivothub.pojo.vo.system.notify.MessageTemplateVo;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

/** 业务服务读取系统消息模板的内部客户端。 */
@FeignClient(name = "pivot-hub-system", contextId = "systemMessageTemplateClient", path = "/system/feign/notify/template")
public interface SystemMessageTemplateClient {
    /** 按消息类型读取模板。 */
    @PostMapping("/get")
    MessageTemplateVo get(@RequestBody MessageTemplateQueryDto query);
}
