package com.pivothub.business.map.service.impl;

import org.springframework.beans.factory.annotation.Autowired;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.pivothub.business.map.config.AmapProperties;
import com.pivothub.business.map.service.AmapService;
import com.pivothub.business.map.utils.AmapHttpClient;
import com.pivothub.business.map.utils.AmapHttpClient.Endpoint;
import com.pivothub.business.map.utils.AmapHttpClient.FailureReason;
import com.pivothub.business.map.utils.AmapHttpClient.FailureStage;
import com.pivothub.business.map.utils.AmapRateLimiter;
import com.pivothub.business.map.utils.AmapSignature;
import com.pivothub.business.map.utils.ClientIpResolver;
import com.pivothub.common.exception.BusinessException;
import com.pivothub.pojo.dto.business.map.AmapCoordinateDto;
import com.pivothub.pojo.dto.business.map.AmapPoiIdDto;
import com.pivothub.pojo.dto.business.map.AmapPoiPageDto;
import com.pivothub.pojo.dto.business.map.AmapRouteRequestDto;
import com.pivothub.pojo.dto.business.map.AmapSearchRequestDto;
import com.pivothub.pojo.vo.business.map.MapCityOptionVo;
import com.pivothub.pojo.vo.business.map.MapCityVo;
import com.pivothub.pojo.vo.business.map.MapRoutePathVo;
import com.pivothub.pojo.vo.business.map.MapRouteVo;
import com.pivothub.pojo.vo.business.map.MapReverseGeocodeVo;
import com.pivothub.pojo.vo.PageVo;
import com.pivothub.pojo.vo.business.map.SearchPoiVo;
import com.pivothub.pojo.vo.business.map.SearchTipVo;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Service;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

@Service
public class AmapServiceImpl implements AmapService {
    @Autowired
    private AmapProperties properties;
    @Autowired
    private AmapHttpClient http;
    @Autowired
    private AmapRateLimiter limiter;
    @Autowired
    private ClientIpResolver ips;
    @Autowired
    private ObjectMapper json;

    @Override public MapCityVo city(HttpServletRequest request, String operatorId) {
        MapCityVo result = new MapCityVo();
        String address = ips.publicIpv4(request).orElse(null);
        if (address == null) return result;
        JsonNode root = get(Endpoint.IP, Map.of("ip", address), operatorId);
        String city = text(root.path("city"));
        if (city == null) return result;
        result.setCityName(city);
        String rectangle = text(root.path("rectangle"));
        if (rectangle != null) {
            List<List<BigDecimal>> points = polyline(rectangle);
            if (points.size() != 2) throw unavailable();
            result.setBounds(Map.of("southWest", points.get(0), "northEast", points.get(1)));
        }
        result.setAvailable(true);
        return result;
    }

    @Override public List<MapCityOptionVo> cities(String operatorId) {
        JsonNode root = get(Endpoint.DISTRICTS,
                Map.of("keywords", "中国", "subdistrict", "3", "extensions", "base"), operatorId);
        try {
            Map<String, MapCityOptionVo> result = new TreeMap<>();
            for (JsonNode country : array(root.path("districts"))) {
                if (!country.isObject()) throw unavailable();
                if (!"country".equals(country.path("level").asText())
                        || !"100000".equals(country.path("adcode").asText())) continue;
                for (JsonNode province : array(country.path("districts"))) {
                    if (!province.isObject()) throw unavailable();
                    String name = districtName(province.path("name"));
                    String adcode = districtAdcode(province.path("adcode"));
                    if (!"province".equals(province.path("level").asText()) || name == null || adcode == null
                            || !(name.endsWith("省") || name.endsWith("自治区") || name.endsWith("市")
                            || name.endsWith("特别行政区"))) continue;
                    if (name.endsWith("市") || "香港特别行政区".equals(name) || "澳门特别行政区".equals(name)) {
                        addCityOption(result, name, name, adcode, null, "province");
                    }
                    for (JsonNode child : array(province.path("districts"))) {
                        collectCities(child, name, null, 2, result);
                    }
                }
            }
            if (result.isEmpty()) throw unavailable();
            return new ArrayList<>(result.values());
        } catch (BusinessException invalid) {
            throw AmapHttpClient.failure(Endpoint.DISTRICTS, FailureReason.UPSTREAM_FORMAT,
                    FailureStage.RESPONSE, null, null);
        }
    }

    private void collectCities(JsonNode node, String province, String parentCity, int remainingDepth,
                               Map<String, MapCityOptionVo> result) {
        if (!node.isObject()) throw unavailable();
        String level = node.path("level").asText();
        if (!"city".equals(level) && !"district".equals(level)) return;
        String name = districtName(node.path("name"));
        String adcode = districtAdcode(node.path("adcode"));
        String childParent = parentCity;
        if (name != null && adcode != null) {
            boolean city = "city".equals(level) && (name.endsWith("市") || name.endsWith("自治州")
                    || name.endsWith("盟") || name.endsWith("地区"));
            if (city || ("district".equals(level) && name.endsWith("市"))) {
                addCityOption(result, province, name, adcode, parentCity, level);
                if (city) childParent = name;
            }
        }
        if (remainingDepth <= 1) return;
        for (JsonNode child : array(node.path("districts"))) {
            collectCities(child, province, childParent, remainingDepth - 1, result);
        }
    }

    private void addCityOption(Map<String, MapCityOptionVo> result, String province, String name,
                               String adcode, String parentCity, String level) {
        MapCityOptionVo option = new MapCityOptionVo();
        option.setProvince(province);
        option.setName(name);
        option.setAdcode(adcode);
        option.setParentCity(parentCity);
        option.setLevel(level);
        result.putIfAbsent(adcode, option);
    }

    private String districtName(JsonNode node) {
        if (!node.isTextual()) return null;
        String value = node.asText().trim();
        if (value.length() < 2 || value.length() > 100
                || value.chars().anyMatch(character -> Character.isISOControl(character)
                || Character.isWhitespace(character) || Character.isSpaceChar(character))) return null;
        return value;
    }

    private String districtAdcode(JsonNode node) {
        if (!node.isTextual() && !node.isIntegralNumber()) return null;
        String value = node.asText();
        return value.matches("[1-9][0-9]{5}") ? value : null;
    }

    @Override public List<SearchTipVo> searchTips(AmapSearchRequestDto request, String operatorId) {
        Map<String, String> parameters = searchParameters(request.getKeyword(), request.getCity());
        parameters.put("datatype", "poi");
        JsonNode root = get(Endpoint.TIPS, parameters, operatorId);
        List<SearchTipVo> result = new ArrayList<>();
        for (JsonNode tip : array(root.path("tips"))) {
            SearchTipVo vo = new SearchTipVo();
            vo.setPoiId(text(tip.path("id")));
            vo.setName(text(tip.path("name")));
            vo.setAddress(text(tip.path("address")));
            vo.setDistrict(text(tip.path("district")));
            vo.setCityCode(cityCode(text(tip.path("citycode")), false));
            List<BigDecimal> point = optionalPoint(text(tip.path("location")));
            if (point != null) { vo.setLongitude(point.get(0)); vo.setLatitude(point.get(1)); }
            result.add(vo);
        }
        return result;
    }

    @Override public PageVo<SearchPoiVo> searchPois(AmapPoiPageDto request, String operatorId) {
        if (request.getPageNum() == null || request.getPageNum() < 1 || request.getPageNum() > 100
                || request.getPageSize() == null || request.getPageSize() < 1 || request.getPageSize() > 20) {
            throw new BusinessException(400, "POI 分页参数无效");
        }
        Map<String, String> parameters = searchParameters(request.getKeyword(), request.getCity());
        parameters.put("page", request.getPageNum().toString());
        parameters.put("offset", request.getPageSize().toString());
        parameters.put("extensions", "base");
        JsonNode root = get(Endpoint.POIS, parameters, operatorId);
        List<SearchPoiVo> items = new ArrayList<>();
        for (JsonNode poi : array(root.path("pois"))) items.add(poi(poi));
        BigDecimal count = nonNegative(root.path("count"));
        try {
            long total = count == null ? (long) items.size() : count.longValueExact();
            Math.addExact(total, request.getPageSize().longValue() - 1);
            return PageVo.of(request.getPageNum(), request.getPageSize(), total, items);
        }
        catch (ArithmeticException invalid) { throw unavailable(); }
    }

    @Override public SearchPoiVo poiDetail(AmapPoiIdDto request, String operatorId) {
        if (request.getPoiId() == null || !request.getPoiId().matches("[A-Za-z0-9]{1,100}")) {
            throw new BusinessException(400, "POI 标识无效");
        }
        JsonNode root = get(Endpoint.POI_DETAIL, Map.of("id", request.getPoiId(), "extensions", "base"), operatorId);
        List<JsonNode> items = array(root.path("pois"));
        if (items.isEmpty()) return null;
        SearchPoiVo result = poi(items.get(0));
        if (result.getLongitude() == null || result.getLatitude() == null) {
            throw new BusinessException(400, "此地点没有有效坐标，请选择具体地点或在地图上选点");
        }
        return result;
    }

    @Override public MapReverseGeocodeVo reverseGeocode(AmapCoordinateDto request, String operatorId) {
        String location = inputPoint(request);
        JsonNode root = get(Endpoint.REGEO, Map.of("location", location, "extensions", "all",
                "radius", "200", "roadlevel", "0"), operatorId);
        JsonNode regeocode = root.path("regeocode");
        if (!regeocode.isObject()) throw unavailable();
        JsonNode component = regeocode.path("addressComponent");
        if (!component.isMissingNode() && !component.isNull() && !component.isObject()) throw unavailable();
        List<String> district = new ArrayList<>();
        for (String field : List.of("province", "city", "district")) {
            String value = text(component.path(field));
            if (value != null && !district.contains(value)) district.add(value);
        }
        MapReverseGeocodeVo result = new MapReverseGeocodeVo();
        result.setLongitude(request.getLongitude());
        result.setLatitude(request.getLatitude());
        result.setAddress(text(regeocode.path("formatted_address")));
        result.setDistrict(String.join("", district));
        String cityCode = cityCode(text(component.path("citycode")), false);
        for (JsonNode candidate : array(regeocode.path("pois"))) {
            SearchPoiVo poi = poi(candidate);
            if (poi.getLongitude() == null || poi.getLatitude() == null) continue;
            if (poi.getDistrict() == null || poi.getDistrict().isBlank()) poi.setDistrict(result.getDistrict());
            if (poi.getCityCode() == null) poi.setCityCode(cityCode);
            result.getPois().add(poi);
            if (result.getPois().size() == 10) break;
        }
        return result;
    }

    @Override public MapRouteVo route(AmapRouteRequestDto request, String operatorId) {
        if (request == null) throw new BusinessException(400, "路线参数无效");
        Endpoint endpoint = switch (request.getMode() == null ? "" : request.getMode()) {
            case "car" -> Endpoint.DRIVING;
            case "walk" -> Endpoint.WALKING;
            case "ride" -> Endpoint.BICYCLING;
            case "bus" -> Endpoint.TRANSIT;
            default -> throw new BusinessException(400, "路线类型无效");
        };
        String start = inputPoint(request.getStart()), end = inputPoint(request.getEnd());
        if (start.equals(end)) throw new BusinessException(400, "起点和终点相同，请重新选择");
        Map<String, String> parameters = new TreeMap<>(Map.of("origin", start, "destination", end));
        // All four v5 services support cost/polyline; transit is parsed as independent segments below.
        parameters.put("show_fields", "cost,polyline");
        if (endpoint == Endpoint.TRANSIT) {
            String originCity = cityCode(request.getOriginCityCode(), true);
            String destinationCity = cityCode(request.getDestinationCityCode(), true);
            if (originCity == null) originCity = regeoCity(start, operatorId);
            if (destinationCity == null) destinationCity = regeoCity(end, operatorId);
            if (originCity == null || destinationCity == null) throw new BusinessException(400, "无法识别公交城市，请选择有效地点");
            parameters.put("city1", originCity);
            parameters.put("city2", destinationCity);
            parameters.put("AlternativeRoute", "1");
        } else parameters.put("alternative_route", "1");
        JsonNode root = get(endpoint, parameters, operatorId);
        try {
            if ("0".equals(root.path("count").asText())) return new MapRouteVo();
            JsonNode route = root.path("route");
            if (!route.isObject()) {
                throw unavailable();
            }
            return endpoint == Endpoint.TRANSIT ? transit(route) : ordinaryRoute(route, request.getMode());
        } catch (BusinessException invalid) {
            throw AmapHttpClient.failure(endpoint, FailureReason.UPSTREAM_FORMAT,
                    endpoint == Endpoint.TRANSIT ? FailureStage.TRANSIT_PARSE : FailureStage.ROUTE_PARSE, null, null);
        }
    }

    private JsonNode get(Endpoint endpoint, Map<String, String> input, String operatorId) {
        AmapProperties.Web web = properties.getWeb();
        if (web == null || web.getKey() == null || web.getKey().isBlank()
                || (web.isSignatureEnabled() && (web.getPrivateKey() == null || web.getPrivateKey().isBlank()))) {
            throw AmapHttpClient.failure(endpoint, FailureReason.CONFIG_MISSING, FailureStage.CONFIG, null, null);
        }
        TreeMap<String, String> parameters = new TreeMap<>(input);
        parameters.remove("sig");
        parameters.put("key", web.getKey());
        parameters.put("output", "json");
        if (web.isSignatureEnabled()) parameters.put("sig", AmapSignature.sign(parameters, web.getPrivateKey()));
        try { limiter.acquire(operatorId); }
        catch (BusinessException failed) {
            if (failed.getHttpStatus() == 429) {
                throw AmapHttpClient.failure(endpoint, FailureReason.RATE_LIMITED, FailureStage.BUDGET, null, null);
            }
            throw failed;
        }
        AmapHttpClient.Response response = http.get(endpoint, parameters, null);
        JsonNode root;
        try {
            root = json.readTree(response.bytes());
        } catch (Exception invalid) {
            throw AmapHttpClient.failure(endpoint, FailureReason.UPSTREAM_FORMAT, FailureStage.RESPONSE, null, null);
        }
        if (root == null || !root.isObject() || !root.path("status").isValueNode()
                || !root.path("infocode").isValueNode()) {
            throw AmapHttpClient.failure(endpoint, FailureReason.UPSTREAM_FORMAT, FailureStage.RESPONSE, null, null);
        }
        String code = root.path("infocode").asText();
        if ("1".equals(root.path("status").asText()) && "10000".equals(code)) return root;
        if (!"0".equals(root.path("status").asText()) || !code.matches("[0-9]{5}")) {
            throw AmapHttpClient.failure(endpoint, FailureReason.UPSTREAM_FORMAT, FailureStage.RESPONSE, null, null);
        }
        if ((endpoint == Endpoint.TRANSIT || endpoint == Endpoint.DRIVING || endpoint == Endpoint.WALKING
                || endpoint == Endpoint.BICYCLING) && ("20801".equals(code) || "20802".equals(code))) {
            return json.createObjectNode().put("count", "0");
        }
        FailureReason reason = switch (code) {
            case "10001", "10013" -> FailureReason.KEY_INVALID;
            case "10009" -> FailureReason.KEY_PLATFORM_MISMATCH;
            case "10002", "10012", "20011" -> FailureReason.SERVICE_PERMISSION;
            case "10041" -> FailureReason.SERVICE_EXPIRED;
            case "10007", "10008" -> FailureReason.SIGNATURE_INVALID;
            case "10005", "10006", "10026" -> FailureReason.ACCESS_RESTRICTED;
            case "10003", "10010", "10044", "10045" -> FailureReason.QUOTA_EXCEEDED;
            case "10004", "10014", "10015", "10019", "10020", "10021", "10029" -> FailureReason.RATE_LIMITED;
            case "20000", "20001", "20002", "20012", "20800" -> FailureReason.UPSTREAM_PARAMETERS;
            default -> FailureReason.UPSTREAM_UNAVAILABLE;
        };
        String info = root.path("info").isTextual() ? root.path("info").asText() : null;
        throw AmapHttpClient.failure(endpoint, reason, FailureStage.RESPONSE, code, info);
    }

    private Map<String, String> searchParameters(String keyword, String city) {
        if (keyword == null || keyword.trim().length() < 2 || keyword.trim().length() > 200
                || keyword.chars().anyMatch(character -> character < 32 || character == 127)) {
            throw new BusinessException(400, "搜索关键词应为 2–200 字");
        }
        Map<String, String> result = new TreeMap<>();
        result.put("keywords", keyword.trim());
        if (city != null && !city.isBlank()) {
            if (city.length() > 100 || city.chars().anyMatch(character -> character < 32 || character == 127)) {
                throw new BusinessException(400, "搜索城市无效");
            }
            result.put("city", city.trim());
            result.put("citylimit", "true");
        }
        return result;
    }

    private SearchPoiVo poi(JsonNode poi) {
        SearchPoiVo vo = new SearchPoiVo();
        vo.setPoiId(text(poi.path("id")));
        vo.setName(text(poi.path("name")));
        vo.setAddress(text(poi.path("address")));
        vo.setCityCode(cityCode(text(poi.path("citycode")), false));
        List<String> district = new ArrayList<>();
        for (String field : List.of("pname", "cityname", "adname")) {
            String value = text(poi.path(field));
            if (value != null && !district.contains(value)) district.add(value);
        }
        vo.setDistrict(String.join("", district));
        List<BigDecimal> point = optionalPoint(text(poi.path("location")));
        if (point != null) { vo.setLongitude(point.get(0)); vo.setLatitude(point.get(1)); }
        return vo;
    }

    private String regeoCity(String point, String operatorId) {
        JsonNode root = get(Endpoint.REGEO, Map.of("location", point, "extensions", "base"), operatorId);
        return cityCode(text(root.path("regeocode").path("addressComponent").path("citycode")), false);
    }

    private String cityCode(String value, boolean input) {
        if (value == null || value.isBlank()) return null;
        if (value.matches("[0-9]{3,4}") && !value.matches("0+")) return value;
        if (input) throw new BusinessException(400, "公交城市编码无效，请选择有效地点");
        return null;
    }

    private MapRouteVo ordinaryRoute(JsonNode route, String mode) {
        MapRouteVo result = new MapRouteVo();
        List<JsonNode> paths = array(route.path("paths"));
        if (paths.isEmpty()) return result;
        JsonNode selected = paths.get(0);
        result.setDistance(nonNegative(selected.path("distance")));
        result.setDuration(duration(selected));
        String kind = "car".equals(mode) ? "drive" : mode;
        for (JsonNode step : array(selected.path("steps"))) {
            addPath(result, text(step.path("polyline")), kind);
            addStep(result, text(step.path("instruction")));
        }
        return result;
    }

    private MapRouteVo transit(JsonNode route) {
        MapRouteVo result = new MapRouteVo();
        if (route.path("transits").isMissingNode()) throw unavailable();
        List<JsonNode> transits = array(route.path("transits"));
        if (transits.isEmpty()) return result;
        JsonNode selected = transits.get(0);
        if (!selected.isObject()) throw unavailable();
        if (selected.path("segments").isMissingNode()) throw unavailable();
        result.setDistance(nonNegative(selected.path("distance")));
        result.setDuration(duration(selected));
        for (JsonNode segment : array(selected.path("segments"))) {
            if (!segment.isObject()) throw unavailable();
            for (String component : List.of("walking", "bus", "railway", "taxi")) {
                JsonNode part = segment.path(component);
                if (!part.isMissingNode() && !part.isNull() && !part.isObject()
                        && !(part.isArray() && part.isEmpty()) && !(part.isTextual() && part.asText().isBlank())) {
                    throw unavailable();
                }
            }
            for (JsonNode walk : array(segment.path("walking").path("steps"))) {
                if (!walk.isObject()) throw unavailable();
                addPath(result, text(walk.path("polyline")), "walk");
                addStep(result, text(walk.path("instruction")));
            }
            List<JsonNode> buslines = array(segment.path("bus").path("buslines"));
            if (buslines.isEmpty()) buslines = array(segment.path("bus").path("steps"));
            if (!buslines.isEmpty()) {
                JsonNode line = buslines.get(0);
                if (!line.isObject()) throw unavailable();
                String name = text(line.path("name")), type = text(line.path("type"));
                String kind = (name != null && (name.contains("地铁") || name.contains("轻轨")))
                        || (type != null && (type.contains("地铁") || type.contains("轻轨"))) ? "subway" : "bus";
                addPath(result, text(line.path("polyline")), kind);
                String departure = text(line.path("departure_stop").path("name"));
                String arrival = text(line.path("arrival_stop").path("name"));
                String instruction = text(line.path("instruction"));
                addStep(result, instruction != null ? instruction : (name == null ? "乘坐公共交通" : name)
                        + (departure == null ? "" : "：" + departure) + (arrival == null ? "" : " → " + arrival));
            }
            JsonNode railway = segment.path("railway");
            List<JsonNode> trains = array(railway.path("steps"));
            if (trains.isEmpty()) {
                addPath(result, text(railway.path("polyline")), "bus");
                addStep(result, text(railway.path("name")));
            } else for (JsonNode train : trains) {
                if (!train.isObject()) throw unavailable();
                addPath(result, text(train.path("polyline")), "bus");
                addStep(result, text(train.path("name")));
            }
            JsonNode taxi = segment.path("taxi");
            addPath(result, text(taxi.path("polyline")), "drive");
            if (taxi.isObject() && !taxi.isEmpty()) addStep(result, "乘坐出租车");
        }
        return result;
    }

    private void addPath(MapRouteVo result, String line, String kind) {
        if (line == null) return;
        List<List<BigDecimal>> coordinates = polyline(line);
        if (coordinates.size() < 2) return;
        MapRoutePathVo path = new MapRoutePathVo();
        path.setKind(kind);
        path.setCoordinates(coordinates);
        result.getPaths().add(path);
    }

    private void addStep(MapRouteVo result, String instruction) {
        if (instruction != null && !instruction.isBlank()) result.getSteps().add(instruction);
    }

    private BigDecimal duration(JsonNode path) {
        BigDecimal duration = nonNegative(path.path("cost").path("duration"));
        return duration == null ? nonNegative(path.path("duration")) : duration;
    }

    private BigDecimal nonNegative(JsonNode value) {
        String raw = text(value);
        if (raw == null) return null;
        try {
            if (raw.length() > 32 || !raw.matches("[0-9]+(\\.[0-9]+)?")) throw unavailable();
            return new BigDecimal(raw);
        } catch (Exception invalid) { throw unavailable(); }
    }

    private List<JsonNode> array(JsonNode value) {
        if (value.isMissingNode() || value.isNull() || (value.isTextual() && value.asText().isBlank())) return List.of();
        if (!value.isArray()) throw unavailable();
        List<JsonNode> result = new ArrayList<>();
        value.forEach(result::add);
        return result;
    }

    private String text(JsonNode node) {
        if (node.isMissingNode() || node.isNull() || (node.isArray() && node.isEmpty())) return null;
        if (!node.isValueNode()) throw unavailable();
        String value = node.asText().trim();
        return value.isEmpty() ? null : value;
    }

    private List<BigDecimal> optionalPoint(String value) {
        if (value == null) return null;
        try { return point(value); }
        catch (BusinessException invalid) { return null; }
    }

    private List<BigDecimal> point(String value) {
        String[] fields = value.split(",", -1);
        if (fields.length != 2) throw unavailable();
        BigDecimal longitude = coordinate(fields[0], 180), latitude = coordinate(fields[1], 90);
        return List.of(longitude.setScale(6, RoundingMode.HALF_UP).stripTrailingZeros(),
                latitude.setScale(6, RoundingMode.HALF_UP).stripTrailingZeros());
    }

    private BigDecimal coordinate(String value, int range) {
        try {
            if (value.length() > 32 || !value.matches("-?[0-9]+(\\.[0-9]+)?")) throw unavailable();
            BigDecimal decimal = new BigDecimal(value);
            if (decimal.abs().compareTo(BigDecimal.valueOf(range)) > 0) throw unavailable();
            return decimal;
        } catch (Exception invalid) { throw unavailable(); }
    }

    private List<List<BigDecimal>> polyline(String value) {
        if (value.length() > 1024 * 1024) throw unavailable();
        List<List<BigDecimal>> coordinates = new ArrayList<>();
        String[] fields = value.split(";", -1);
        if (fields.length == 1) {
            String[] flattened = value.split(",", -1);
            if (flattened.length % 2 != 0) throw unavailable();
            for (int i = 0; i < flattened.length; i += 2) coordinates.add(point(flattened[i] + "," + flattened[i + 1]));
        } else for (String field : fields) coordinates.add(point(field));
        if (coordinates.size() > 50000) throw unavailable();
        return coordinates;
    }

    private String inputPoint(AmapCoordinateDto point) {
        if (point == null || point.getLongitude() == null || point.getLatitude() == null
                || point.getLongitude().abs().compareTo(BigDecimal.valueOf(180)) > 0
                || point.getLatitude().abs().compareTo(BigDecimal.valueOf(90)) > 0
                || point.getLongitude().stripTrailingZeros().scale() > 6 || point.getLatitude().stripTrailingZeros().scale() > 6) {
            throw new BusinessException(400, "请提供有效经纬度，最多保留 6 位小数");
        }
        return point.getLongitude().stripTrailingZeros().toPlainString() + "," + point.getLatitude().stripTrailingZeros().toPlainString();
    }

    private BusinessException unavailable() { return new BusinessException(503, "地图服务暂不可用，请稍后重试"); }
}
