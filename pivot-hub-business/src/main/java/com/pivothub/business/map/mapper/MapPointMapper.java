package com.pivothub.business.map.mapper;
import com.pivothub.pojo.po.business.map.MapPoint;
import com.pivothub.pojo.vo.business.map.*;
import com.pivothub.pojo.dto.business.map.MapPointQueryDto;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Mapper;
import java.util.List;
@Mapper
public interface MapPointMapper {
    MapPoint selectById(@Param("owner") String owner, @Param("id") String id);
    MapPoint selectByIdForUpdate(@Param("owner") String owner, @Param("id") String id);
    int insert(MapPoint record);
    int update(MapPoint record);
    int markDeleted(@Param("owner") String owner, @Param("id") String id);
    List<MapPointSummaryVo> selectSummaries(@Param("owner") String owner, @Param("query") MapPointQueryDto query);
    List<MapPointSummaryVo> selectMarkers(@Param("owner") String owner, @Param("query") MapPointQueryDto query);
    MapPointVo selectDetail(@Param("owner") String owner, @Param("id") String id);
    long countThemePoints(@Param("owner") String owner, @Param("themeId") String themeId);
    long countCategoryReferences(@Param("owner") String owner, @Param("themeId") String themeId, @Param("categoryId") String categoryId);
    String findPoiId(@Param("owner") String owner, @Param("themeId") String themeId, @Param("poiId") String poiId);
    int deleteByTheme(@Param("owner") String owner, @Param("themeId") String themeId);
}
