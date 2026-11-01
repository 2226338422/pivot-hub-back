package com.pivothub.system.mapper;

import com.pivothub.pojo.po.system.SysFile;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/** 文件记录表 Mapper（包扫描注入，无需 @Mapper 注解）。 */
public interface SysFileMapper {

    int insert(SysFile file);

    /** 按主键批量查询。 */
    List<SysFile> selectByUuids(@Param("uuids") List<String> uuids);

    /** 按文件类型取一条（用于默认头像等）。 */
    SysFile selectFirstByType(@Param("type") String type);
}
