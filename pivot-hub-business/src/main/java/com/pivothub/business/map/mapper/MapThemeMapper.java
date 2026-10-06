package com.pivothub.business.map.mapper;
import com.pivothub.pojo.po.business.map.MapTheme;
import com.pivothub.pojo.vo.business.map.*;
import com.pivothub.pojo.dto.business.map.MapPointQueryDto;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Mapper;
import java.util.List;
@Mapper
public interface MapThemeMapper {
    MapTheme selectById(@Param("owner") String owner, @Param("id") String id);
    MapTheme selectByIdForUpdate(@Param("owner") String owner, @Param("id") String id);
    int insert(MapTheme record);
    int update(MapTheme record);
    int markDeleted(@Param("owner") String owner, @Param("id") String id);
    List<MapTheme> selectList(@Param("owner") String owner);
}
