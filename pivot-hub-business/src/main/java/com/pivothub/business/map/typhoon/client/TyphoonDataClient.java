package com.pivothub.business.map.typhoon.client;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.pivothub.business.map.typhoon.config.TyphoonProperties;
import com.pivothub.common.exception.BusinessException;
import com.pivothub.commoncore.constants.business.map.TyphoonConstants;
import com.pivothub.pojo.vo.business.map.typhoon.TyphoonDetailVo;
import com.pivothub.pojo.vo.business.map.typhoon.TyphoonForecastPointVo;
import com.pivothub.pojo.vo.business.map.typhoon.TyphoonForecastVo;
import com.pivothub.pojo.vo.business.map.typhoon.TyphoonListVo;
import com.pivothub.pojo.vo.business.map.typhoon.TyphoonObservationVo;
import com.pivothub.pojo.vo.business.map.typhoon.TyphoonSummaryVo;
import com.pivothub.pojo.vo.business.map.typhoon.TyphoonWindCircleVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLParameters;
import javax.net.ssl.TrustManagerFactory;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;
import java.security.KeyStore;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.time.format.ResolverStyle;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.Flow;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/** 获取中央气象台网站台风数据，严格解析JSONP并统一实况、预报和风圈字段。 */
@Component
public class TyphoonDataClient {

    @Autowired
    private TyphoonProperties properties;

    private final HttpClient httpClient = createHttpClient();

    private final ObjectMapper objectMapper = new ObjectMapper()
            .enable(DeserializationFeature.FAIL_ON_TRAILING_TOKENS);

    /** 仅为台风客户端加载项目CA证书，保留证书链与HTTPS域名校验，不修改JVM全局信任库。 */
    private static HttpClient createHttpClient() {
        try (InputStream input = TyphoonDataClient.class.getResourceAsStream("/certs/nmc-ca.pem")) {
            if (input == null) {
                throw new IOException("缺少台风数据源CA证书：/certs/nmc-ca.pem");
            }
            KeyStore trustStore = KeyStore.getInstance("PKCS12");
            trustStore.load(null, null);
            int index = 0;
            for (Certificate certificate : CertificateFactory.getInstance("X.509").generateCertificates(input)) {
                if (!(certificate instanceof X509Certificate ca) || ca.getBasicConstraints() < 0) {
                    throw new CertificateException("台风信任库仅允许CA证书");
                }
                ca.checkValidity();
                trustStore.setCertificateEntry("nmc-ca-" + index++, ca);
            }
            if (index == 0) {
                throw new CertificateException("台风数据源CA证书为空");
            }
            TrustManagerFactory managers = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
            managers.init(trustStore);
            SSLContext context = SSLContext.getInstance("TLS");
            context.init(null, managers.getTrustManagers(), null);
            SSLParameters parameters = new SSLParameters();
            parameters.setEndpointIdentificationAlgorithm("HTTPS");
            return HttpClient.newBuilder()
                    .sslContext(context)
                    .sslParameters(parameters)
                    .connectTimeout(Duration.ofSeconds(3))
                    .followRedirects(HttpClient.Redirect.NEVER)
                    .build();
        } catch (IOException | GeneralSecurityException exception) {
            throw new IllegalStateException("初始化台风HTTPS客户端失败", exception);
        }
    }

    public TyphoonListVo fetchList() {
        return parseList(request("list_default"));
    }

    public TyphoonDetailVo fetchDetail(String typhoonId) {
        if (typhoonId == null || !typhoonId.matches("[0-9]{1,12}")) {
            throw new BusinessException(400, "台风标识格式错误");
        }
        return parseDetail(request("view_" + typhoonId), typhoonId);
    }

    private URI resolveUri(String path) {
        String baseUrl = properties.getBaseUrl();
        if (baseUrl == null || baseUrl.isBlank()) {
            throw new BusinessException(503, "缺少台风数据源配置 typhoon.base-url");
        }
        try {
            URI baseUri = URI.create(baseUrl.trim());
            if (!"https".equalsIgnoreCase(baseUri.getScheme()) || baseUri.getHost() == null
                    || baseUri.getRawUserInfo() != null || baseUri.getRawQuery() != null
                    || baseUri.getRawFragment() != null) {
                throw new IllegalArgumentException("台风数据源必须是有效的HTTPS地址");
            }
            String normalizedBase = baseUri.toString();
            return URI.create(normalizedBase.endsWith("/") ? normalizedBase : normalizedBase + "/")
                    .resolve(path);
        } catch (IllegalArgumentException exception) {
            throw new BusinessException(503, "台风数据源配置 typhoon.base-url 无效，请配置HTTPS基础地址");
        }
    }

    private String request(String resource) {
        if (!properties.isEnabled()) {
            throw unavailable();
        }
        URI requestUri = resolveUri("weatherservice/typhoon/jsons/" + resource);
        int timeout = Math.max(1, Math.min(properties.getTimeoutSeconds(), 30));
        HttpRequest request = HttpRequest.newBuilder(requestUri)
                .timeout(Duration.ofSeconds(timeout))
                .header("Accept", "application/json, text/javascript, */*;q=0.5")
                .header("Referer", resolveUri("web.html").toString())
                .header("User-Agent", "PivotHub/1.0")
                .GET().build();
        CompletableFuture<HttpResponse<byte[]>> pending = httpClient.sendAsync(request, info -> limitedBody());
        try {
            HttpResponse<byte[]> response = pending.get(timeout, TimeUnit.SECONDS);
            if (response.statusCode() != 200) {
                throw unavailable();
            }
            return new String(response.body(), StandardCharsets.UTF_8);
        } catch (InterruptedException exception) {
            pending.cancel(true);
            Thread.currentThread().interrupt();
            throw unavailable(exception);
        } catch (Exception exception) {
            pending.cancel(true);
            throw unavailable(exception);
        }
    }

    private HttpResponse.BodySubscriber<byte[]> limitedBody() {
        HttpResponse.BodySubscriber<byte[]> delegate = HttpResponse.BodySubscribers.ofByteArray();
        return new HttpResponse.BodySubscriber<>() {
            private Flow.Subscription subscription;
            private long received;
            private boolean failed;

            @Override
            public CompletionStage<byte[]> getBody() {
                return delegate.getBody();
            }

            @Override
            public void onSubscribe(Flow.Subscription value) {
                subscription = value;
                delegate.onSubscribe(value);
            }

            @Override
            public void onNext(List<ByteBuffer> buffers) {
                if (failed) {
                    return;
                }
                for (ByteBuffer buffer : buffers) {
                    received += buffer.remaining();
                }
                if (received > 2 * 1024 * 1024) {
                    failed = true;
                    subscription.cancel();
                    delegate.onError(new IOException("NMC response exceeds size limit"));
                    return;
                }
                delegate.onNext(buffers);
            }

            @Override
            public void onError(Throwable throwable) {
                if (!failed) {
                    delegate.onError(throwable);
                }
            }

            @Override
            public void onComplete() {
                if (!failed) {
                    delegate.onComplete();
                }
            }
        };
    }

    private TyphoonListVo parseList(String body) {
        JsonNode rows = parseJsonp(body, "typhoon_jsons_list_default").get("typhoonList");
        if (rows == null || !rows.isArray()) {
            throw unavailable();
        }
        List<TyphoonSummaryVo> typhoons = new ArrayList<>();
        for (JsonNode row : rows) {
            TyphoonSummaryVo summary = summary(row);
            if (summary.isActive()) {
                typhoons.add(summary);
            }
        }
        typhoons.sort(Comparator.comparingLong((TyphoonSummaryVo value) -> Long.parseLong(value.getId())).reversed());
        TyphoonListVo result = new TyphoonListVo();
        result.setTyphoons(typhoons);
        result.setSource(TyphoonConstants.SOURCE);
        result.setSourceUrl(resolveUri("web.html").toString());
        return result;
    }

    private TyphoonDetailVo parseDetail(String body, String typhoonId) {
        JsonNode row = parseJsonp(body, "typhoon_jsons_view_" + typhoonId).get("typhoon");
        TyphoonSummaryVo summary = summary(row);
        if (!typhoonId.equals(summary.getId()) || row.size() < 9 || !row.get(8).isArray()) {
            throw unavailable();
        }
        List<TyphoonObservationVo> observations = new ArrayList<>();
        JsonNode latest = null;
        String latestTime = null;
        for (JsonNode point : row.get(8)) {
            TyphoonObservationVo observation = observation(point);
            if (observation != null) {
                observations.add(observation);
                if (latestTime == null || observation.getTime().compareTo(latestTime) >= 0) {
                    latest = point;
                    latestTime = observation.getTime();
                }
            }
        }
        if (observations.isEmpty()) {
            throw unavailable();
        }
        observations.sort(Comparator.comparing(TyphoonObservationVo::getTime));
        TyphoonObservationVo last = observations.get(observations.size() - 1);
        summary.setObservedAt(last.getTime());
        summary.setLongitude(last.getLongitude());
        summary.setLatitude(last.getLatitude());
        summary.setLevel(last.getLevel());
        TyphoonDetailVo result = new TyphoonDetailVo();
        result.setTyphoon(summary);
        result.setObservations(observations);
        result.setForecast(forecast(latest));
        result.setSource(TyphoonConstants.SOURCE);
        result.setSourceUrl(resolveUri("web.html").toString());
        result.setSourceUpdatedAt(latestTime);
        return result;
    }

    private JsonNode parseJsonp(String body, String expectedCallback) {
        if (body == null || body.length() > 2 * 1024 * 1024) {
            throw unavailable();
        }
        Matcher matcher = Pattern.compile("^\\s*([A-Za-z_$][A-Za-z0-9_$]*)\\s*\\((.*)\\)\\s*;?\\s*$", Pattern.DOTALL).matcher(body);
        if (!matcher.matches() || !expectedCallback.equals(matcher.group(1))) {
            throw unavailable();
        }
        String payload = matcher.group(2).trim();
        if (payload.startsWith("(") && payload.endsWith(")")) {
            payload = payload.substring(1, payload.length() - 1).trim();
        }
        try {
            JsonNode root = objectMapper.readTree(payload);
            if (root == null || !root.isObject()) {
                throw unavailable();
            }
            return root;
        } catch (IOException exception) {
            throw unavailable();
        }
    }

    private TyphoonSummaryVo summary(JsonNode row) {
        if (row == null || !row.isArray() || row.size() < 8) {
            throw unavailable();
        }
        String id = text(row.get(0));
        String status = text(row.get(7));
        if (id == null || !id.matches("[0-9]{1,12}")
                || !("start".equals(status) || "stop".equals(status))) {
            throw unavailable();
        }
        TyphoonSummaryVo result = new TyphoonSummaryVo();
        result.setId(id);
        String name = text(row.get(2));
        result.setName(name == null ? text(row.get(1)) : name);
        result.setNumber(text(row.get(3)));
        result.setActive("start".equals(status));
        return result;
    }

    private TyphoonObservationVo observation(JsonNode point) {
        if (!point.isArray() || point.size() < 10) {
            return null;
        }
        String time = utc(point.get(1));
        Double longitude = number(point.get(4), -180, 180);
        Double latitude = number(point.get(5), -90, 90);
        if (time == null || longitude == null || latitude == null) {
            return null;
        }
        TyphoonObservationVo result = new TyphoonObservationVo();
        result.setTime(time);
        result.setLongitude(longitude);
        result.setLatitude(latitude);
        result.setLevel(text(point.get(3)));
        result.setPressure(number(point.get(6), 800, 1100));
        result.setWindSpeed(number(point.get(7), 0, 150));
        result.setMoveDirection(text(point.get(8)));
        result.setMoveSpeed(number(point.get(9), 0, 300));
        result.setWindCircles(windCircles(point.get(10)));
        return result;
    }

    private List<TyphoonWindCircleVo> windCircles(JsonNode rows) {
        List<TyphoonWindCircleVo> result = new ArrayList<>();
        if (rows == null || !rows.isArray()) {
            return result;
        }
        for (JsonNode row : rows) {
            if (!row.isArray() || row.size() < 5) {
                continue;
            }
            Integer level = switch (String.valueOf(text(row.get(0)))) {
                case "30KTS" -> 7;
                case "50KTS" -> 10;
                case "64KTS" -> 12;
                default -> null;
            };
            if (level == null) {
                continue;
            }
            TyphoonWindCircleVo circle = new TyphoonWindCircleVo();
            circle.setLevel(level);
            circle.setNortheast(number(row.get(1), 0, 5000));
            circle.setSoutheast(number(row.get(2), 0, 5000));
            circle.setSouthwest(number(row.get(3), 0, 5000));
            circle.setNorthwest(number(row.get(4), 0, 5000));
            if (circle.getNortheast() != null || circle.getSoutheast() != null
                    || circle.getSouthwest() != null || circle.getNorthwest() != null) {
                result.add(circle);
            }
        }
        return result;
    }

    private TyphoonForecastVo forecast(JsonNode observation) {
        JsonNode agencies = observation.get(11);
        if (agencies == null || !agencies.isObject() || agencies.isEmpty()) {
            return null;
        }
        List<String> codes = new ArrayList<>();
        agencies.fieldNames().forEachRemaining(code -> {
            if (code.matches("[A-Z0-9]{2,12}")) {
                codes.add(code);
            }
        });
        codes.sort(Comparator.comparing((String code) -> !"BABJ".equals(code)).thenComparing(code -> code));
        for (String code : codes) {
            JsonNode rows = agencies.get(code);
            if (!rows.isArray()) {
                continue;
            }
            List<TyphoonForecastPointVo> points = new ArrayList<>();
            String issuedAt = null;
            for (JsonNode row : rows) {
                if (!row.isArray() || row.size() < 8 || !code.equals(text(row.get(6)))) {
                    continue;
                }
                Double hours = number(row.get(0), 1, 240);
                String issued = utc(row.get(1));
                Double longitude = number(row.get(2), -180, 180);
                Double latitude = number(row.get(3), -90, 90);
                if (hours == null || hours % 1 != 0 || issued == null || longitude == null || latitude == null) {
                    continue;
                }
                if (issuedAt != null && !issuedAt.equals(issued)) {
                    continue;
                }
                issuedAt = issued;
                TyphoonForecastPointVo point = new TyphoonForecastPointVo();
                point.setTime(Instant.parse(issued).plusSeconds(hours.longValue() * 3600).toString());
                point.setLongitude(longitude);
                point.setLatitude(latitude);
                point.setPressure(number(row.get(4), 800, 1100));
                point.setWindSpeed(number(row.get(5), 0, 150));
                point.setLevel(text(row.get(7)));
                points.add(point);
            }
            if (!points.isEmpty()) {
                points.sort(Comparator.comparing(TyphoonForecastPointVo::getTime));
                TyphoonForecastVo result = new TyphoonForecastVo();
                result.setAgency(code);
                result.setIssuedAt(issuedAt);
                result.setUpdatedAt(issuedAt);
                result.setPoints(points);
                return result;
            }
        }
        return null;
    }

    private String utc(JsonNode value) {
        String text = text(value);
        if (text == null || !text.matches("[0-9]{12}")) {
            return null;
        }
        try {
            return LocalDateTime.parse(text, DateTimeFormatter.ofPattern("uuuuMMddHHmm")
                    .withResolverStyle(ResolverStyle.STRICT)).toInstant(ZoneOffset.UTC).toString();
        } catch (RuntimeException exception) {
            return null;
        }
    }

    private Double number(JsonNode value, double minimum, double maximum) {
        if (value == null || !value.isNumber()) {
            return null;
        }
        double number = value.doubleValue();
        return Double.isFinite(number) && number >= minimum && number <= maximum ? number : null;
    }

    private String text(JsonNode value) {
        if (value == null || !(value.isTextual() || value.isIntegralNumber())) {
            return null;
        }
        String text = value.asText().trim();
        return text.isEmpty() ? null : text;
    }

    private BusinessException unavailable() {
        return new BusinessException(503, "中央气象台台风数据暂不可用");
    }

    private BusinessException unavailable(Throwable cause) {
        BusinessException error = unavailable();
        error.initCause(cause);
        return error;
    }
}
