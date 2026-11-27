package com.pivothub.business.map.typhoon.utils;

import com.pivothub.business.map.amap.service.AmapService;
import com.pivothub.business.map.typhoon.service.TyphoonService;
import com.pivothub.business.map.typhoon.utils.TyphoonForecastMatcher.Boundary;
import com.pivothub.business.map.typhoon.utils.TyphoonForecastMatcher.Point;
import com.pivothub.business.map.typhoon.utils.TyphoonForecastMatcher.TimedPoint;
import com.pivothub.pojo.dto.business.map.typhoon.TyphoonProvinceForecastResultDto;
import com.pivothub.pojo.dto.business.map.typhoon.TyphoonProvinceMatchDto;
import com.pivothub.pojo.vo.business.map.amap.MapCoordinateVo;
import com.pivothub.pojo.vo.business.map.amap.MapProvinceBoundaryVo;
import com.pivothub.pojo.vo.business.map.typhoon.TyphoonForecastPointVo;
import com.pivothub.pojo.vo.business.map.typhoon.TyphoonForecastVo;
import com.pivothub.pojo.vo.business.map.typhoon.TyphoonSummaryVo;
import lombok.Value;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * 判断台风预测路径是否经过指定省份，返回命中的省份及不可用的台风路径和省界。
 */
@Component
public class TyphoonProvinceForecast {
    private static final Logger LOG = LoggerFactory.getLogger(TyphoonProvinceForecast.class);
    private static final String AMAP_OPERATOR = "typhoon-forecast";

    @Autowired
    private AmapService amapService;
    @Autowired
    private TyphoonService typhoonService;

    @Value
    private static class ProvinceArea {
        String name;
        Boundary boundary;
    }

    /**
     * @Description: 一次运行内按台风及省级行政区编码计算预报路径命中情况
     * @param storms 活跃台风
     * @param provinceCodes 本次需要匹配的省级行政区编码
     * @param startedAt 本次运行的开始时刻
     * @param replayAt 历史回放时刻，实时执行时为空
     * @return 各台风命中的省份，以及无法查询的台风及省份
     */
    public TyphoonProvinceForecastResultDto match(List<TyphoonSummaryVo> storms, Set<String> provinceCodes,
                                                  Instant startedAt, Instant replayAt) {
        TyphoonProvinceForecastResultDto result = new TyphoonProvinceForecastResultDto();
        if (provinceCodes.isEmpty()) {
            return result;
        }
        Set<String> unavailableProvinces = result.getUnavailableProvinceCodes();
        Map<String, ProvinceArea> boundaries = new LinkedHashMap<>();
        Instant forecastStart = replayAt == null ? startedAt : replayAt;
        for (TyphoonSummaryVo storm : storms) {
            List<TimedPoint> path;
            try {
                TyphoonForecastVo forecast = typhoonService.detail(storm.getId(), replayAt).getForecast();
                path = forecastPath(forecast, forecastStart);
            } catch (RuntimeException failed) {
                result.getUnavailableStormIds().add(storm.getId());
                LOG.warn("台风预报路径不可用 typhoonId={}", storm.getId(), failed);
                continue;
            }
            if (path.isEmpty()) {
                continue;
            }
            if (boundaries.isEmpty() && unavailableProvinces.isEmpty()) {
                for (String provinceCode : provinceCodes) {
                    try {
                        boundaries.put(provinceCode, boundary(provinceCode));
                    } catch (RuntimeException failed) {
                        unavailableProvinces.add(provinceCode);
                        LOG.warn("省级行政区边界不可用 adcode={}", provinceCode, failed);
                    }
                }
            }
            Map<String, TyphoonProvinceMatchDto> matchingProvinces = new LinkedHashMap<>();
            boundaries.forEach((code, area) -> {
                if (TyphoonForecastMatcher.match(path, area.getBoundary(), forecastStart)) {
                    TyphoonProvinceMatchDto match = new TyphoonProvinceMatchDto();
                    match.setProvinceName(area.getName());
                    matchingProvinces.put(code, match);
                }
            });
            if (!matchingProvinces.isEmpty()) {
                result.getMatches().put(storm.getId(), matchingProvinces);
            }
        }
        return result;
    }

    private List<TimedPoint> forecastPath(TyphoonForecastVo forecast, Instant forecastStart) {
        if (forecast == null || forecast.getPoints() == null || forecast.getPoints().isEmpty()) {
            return new ArrayList<>();
        }
        List<TyphoonForecastPointVo> points = forecast.getPoints().stream()
                .sorted(Comparator.comparing(point -> Instant.parse(point.getTime())))
                .toList();
        List<Instant> times = points.stream().map(point -> Instant.parse(point.getTime())).toList();
        int firstFuture = 0;
        while (firstFuture < times.size() && times.get(firstFuture).isBefore(forecastStart)) {
            firstFuture++;
        }
        if (firstFuture == times.size()) {
            return new ArrayList<>();
        }
        int start = Math.max(0, firstFuture - 1);
        List<MapCoordinateVo> gps = points.subList(start, points.size()).stream()
                .map(point -> new MapCoordinateVo(point.getLongitude(), point.getLatitude()))
                .toList();
        List<MapCoordinateVo> converted = amapService.convertGpsCoordinates(gps, AMAP_OPERATOR);
        List<TimedPoint> path = new ArrayList<>(converted.size());
        for (int index = 0; index < converted.size(); index++) {
            path.add(new TimedPoint(asPoint(converted.get(index)), times.get(start + index)));
        }
        return path;
    }

    private ProvinceArea boundary(String provinceCode) {
        MapProvinceBoundaryVo province = amapService.provinceBoundary(provinceCode, AMAP_OPERATOR);
        List<List<Point>> coordinates = province.getOutlines().stream()
                .map(outline -> outline.stream().map(TyphoonProvinceForecast::asPoint).toList())
                .toList();
        return new ProvinceArea(province.getName(), new Boundary(coordinates));
    }

    private static Point asPoint(MapCoordinateVo coordinate) {
        return new Point(coordinate.getLongitude(), coordinate.getLatitude());
    }
}
