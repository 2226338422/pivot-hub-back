package com.pivothub.system.notify.mapper;

import com.pivothub.pojo.dto.system.notify.MessageTemplatePageDto;
import com.pivothub.pojo.dto.system.notify.MessageTemplateSaveDto;
import com.pivothub.pojo.po.system.SysMessageTemplate;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/** 系统消息模板表 Mapper。 */
@Mapper
public interface SysMessageTemplateMapper {
    /** 按消息类型查询模板。 */
    SysMessageTemplate selectByType(@Param("messageType") String messageType);

    /** 按 UUID 查询模板。 */
    SysMessageTemplate selectByUuid(@Param("uuid") String uuid);

    /** 按消息类型编码或名称模糊查询模板。 */
    List<SysMessageTemplate> selectPage(MessageTemplatePageDto query);

    /** 按 UUID 修改正文和明细模板。 */
    int updateContent(MessageTemplateSaveDto request);

    /** 判断指定 UUID 的模板是否存在。 */
    boolean existsByUuid(@Param("uuid") String uuid);
}
