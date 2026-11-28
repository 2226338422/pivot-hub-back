package com.pivothub.system.email.mapper;

import com.pivothub.pojo.po.system.SysMailTemplate;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/** 邮件模板表 Mapper。 */
@Mapper
public interface SysMailTemplateMapper {
    /** 按模板编码查询启用的模板。 */
    SysMailTemplate selectEnabledByCode(@Param("templateCode") String templateCode);
}
