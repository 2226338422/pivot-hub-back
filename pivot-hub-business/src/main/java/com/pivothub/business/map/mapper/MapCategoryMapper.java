package com.pivothub.business.map.mapper;
import com.pivothub.pojo.po.business.map.MapCategory;
import com.pivothub.pojo.vo.business.map.*;
import com.pivothub.pojo.dto.business.map.MapPointQueryDto;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Mapper;
import java.util.List;
@Mapper
public interface MapCategoryMapper {
    MapCategory selectById(@Param("owner") String owner, @Param("id") String id);
    MapCategory selectByIdForUpdate(@Param("owner") String owner, @Param("id") String id);
    int insert(MapCategory record);
    int update(MapCategory record);
    int markDeleted(@Param("owner") String owner, @Param("id") String id);
    List<MapCategory> selectList(@Param("owner") String owner, @Param("themeId") String themeId);
    long countName(@Param("owner") String owner, @Param("themeId") String themeId, @Param("name") String name, @Param("excludeId") String excludeId);
    long countIconReferences(@Param("owner") String owner, @Param("iconId") String iconId);
    int deleteByTheme(@Param("owner") String owner, @Param("themeId") String themeId);
}
