package com.pivothub.system.announcement.mapper;

import com.pivothub.pojo.dto.system.announcement.AnnouncementPageDto;
import com.pivothub.pojo.dto.system.announcement.AnnouncementFeedQueryDto;
import com.pivothub.pojo.po.system.SysAnnouncement;
import com.pivothub.pojo.vo.system.announcement.AnnouncementLatestVo;
import com.pivothub.pojo.vo.system.announcement.AnnouncementVo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/** 公告主表查询与状态变更。 */
@Mapper
public interface SysAnnouncementMapper {
    /** 查询最近发布的一条公告。 */
    AnnouncementLatestVo selectLatestPublished();

    /** 根据上一批末条公告UUID定位发布时间，查询下一批已发布公告。 */
    List<AnnouncementVo> selectFeed(@Param("query") AnnouncementFeedQueryDto query,
                                     @Param("limitPlusOne") int limitPlusOne);

    /** 按筛选条件分页查询公告管理数据。 */
    List<AnnouncementVo> selectPage(@Param("query") AnnouncementPageDto query);

    /** 按主键查询公告及发布人展示信息。 */
    AnnouncementVo selectById(@Param("id") String id);

    /** 锁定公告主表记录以校验状态转换。 */
    SysAnnouncement selectByIdForUpdate(@Param("id") String id);

    /** 插入公告草稿。 */
    int insert(SysAnnouncement announcement);

    /** 仅更新草稿的标题和正文。 */
    int updateDraft(@Param("id") String id, @Param("title") String title,
                    @Param("content") String content, @Param("operatorId") String operatorId);

    /** 图片关联变化时更新草稿修改时间。 */
    int touchDraft(@Param("id") String id, @Param("operatorId") String operatorId);

    /** 将草稿发布并记录发布时间。 */
    int publish(@Param("id") String id, @Param("operatorId") String operatorId);

    /** 撤回已发布公告。 */
    int withdraw(@Param("id") String id, @Param("operatorId") String operatorId);
}
