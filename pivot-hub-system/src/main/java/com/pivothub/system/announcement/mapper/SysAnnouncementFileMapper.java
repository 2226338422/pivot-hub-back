package com.pivothub.system.announcement.mapper;

import com.pivothub.pojo.po.system.SysAnnouncementFile;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/** 公告与附件关联记录的查询和维护。 */
@Mapper
public interface SysAnnouncementFileMapper {
    /** 按公告ID批量读取有序附件关联。 */
    List<SysAnnouncementFile> selectByAnnouncementIds(@Param("announcementIds") List<String> announcementIds);

    /** 删除指定公告的全部附件关联。 */
    int deleteByAnnouncementId(@Param("announcementId") String announcementId);

    /** 批量新增公告附件关联。 */
    int insertBatch(@Param("relations") List<SysAnnouncementFile> relations);
}
