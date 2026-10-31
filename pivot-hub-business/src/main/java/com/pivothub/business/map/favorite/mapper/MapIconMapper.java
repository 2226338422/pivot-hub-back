package com.pivothub.business.map.favorite.mapper;
import com.pivothub.pojo.po.business.map.favorite.MapIcon;
import com.pivothub.pojo.vo.business.map.favorite.*;
import com.pivothub.pojo.dto.business.map.favorite.MapPointQueryDto;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Mapper;
import java.util.List;
@Mapper
public interface MapIconMapper {
    MapIcon selectById(@Param("owner") String owner, @Param("id") String id);
    MapIcon selectByIdForUpdate(@Param("owner") String owner, @Param("id") String id);
    int insert(MapIcon record);
    int update(MapIcon record);
    int markDeleted(@Param("owner") String owner, @Param("id") String id);
    List<MapIcon> selectList(@Param("owner") String owner, @Param("keyword") String keyword);
}
