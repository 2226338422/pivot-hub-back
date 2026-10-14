package com.pivothub.business.map.utils;

import com.pivothub.common.exception.BusinessException;
import org.springframework.stereotype.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.io.ByteArrayOutputStream;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpTimeoutException;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.Flow;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

/**
 * @Description: 高德HTTP请求工具，使用固定HTTPS服务地址并限制超时、响应大小及重定向
 * @Author lhb
 * @CreateTime 2026/10/5
 */
@Component
public class AmapHttpClient {
    private static final Logger LOG = LoggerFactory.getLogger(AmapHttpClient.class);
    private static final int MAX_BODY_BYTES = 2 * 1024 * 1024;
    private final HttpClient client = HttpClient.newBuilder().connectTimeout(Duration.ofSeconds(3))
            .followRedirects(HttpClient.Redirect.NEVER).build();

    public enum Endpoint {
        STYLES("webapi.amap.com", "/v4/map/styles"),
        IP("restapi.amap.com", "/v3/ip"), IP_V5("restapi.amap.com", "/v5/ip"),
        REGEO("restapi.amap.com", "/v3/geocode/regeo"), GEO("restapi.amap.com", "/v3/geocode/geo"),
        CONVERT("restapi.amap.com", "/v3/coordinate/convert"),
        DISTRICTS("restapi.amap.com", "/v3/config/district"),
        TIPS("restapi.amap.com", "/v3/assistant/inputtips"),
        POIS("restapi.amap.com", "/v3/place/text"), POI_DETAIL("restapi.amap.com", "/v3/place/detail"),
        DRIVING("restapi.amap.com", "/v5/direction/driving"),
        WALKING("restapi.amap.com", "/v5/direction/walking"),
        BICYCLING("restapi.amap.com", "/v5/direction/bicycling"),
        TRANSIT("restapi.amap.com", "/v5/direction/transit/integrated");
        private final String host;
        private final String path;
        Endpoint(String host, String path) { this.host = host; this.path = path; }
        public String getPath() { return path; }
    }

    public record Response(int status, String contentType, byte[] bytes) {}

    public enum FailureReason {
        CONFIG_MISSING, KEY_INVALID, KEY_PLATFORM_MISMATCH, SERVICE_PERMISSION, SERVICE_EXPIRED,
        SIGNATURE_INVALID, ACCESS_RESTRICTED, QUOTA_EXCEEDED, RATE_LIMITED,
        UPSTREAM_TIMEOUT, UPSTREAM_NETWORK, UPSTREAM_FORMAT, UPSTREAM_UNAVAILABLE, UPSTREAM_PARAMETERS
    }

    public enum FailureStage { CONFIG, BUDGET, HTTP, RESPONSE, TRANSIT_PARSE, ROUTE_PARSE }

    public static BusinessException failure(Endpoint endpoint, FailureReason reason, FailureStage stage,
                                            String providerCode, String info) {
        String safeCode = providerCode != null && providerCode.matches("[0-9]{5}") ? providerCode : null;
        String safeInfo = info != null && info.matches("[A-Z_]{1,80}") ? info : "UNKNOWN";
        LOG.warn("Amap endpoint={} stage={} providerCode={} info={}", endpoint.name(), stage.name(),
                safeCode == null ? "UNKNOWN" : safeCode, safeInfo);
        Map<String, String> data = new java.util.TreeMap<>();
        data.put("reasonCode", reason.name());
        data.put("operation", endpoint.name());
        if (safeCode != null) data.put("providerCode", safeCode);
        boolean limited = reason == FailureReason.QUOTA_EXCEEDED || reason == FailureReason.RATE_LIMITED;
        return new BusinessException(limited ? 429 : 503, limited
                ? "地图请求额度或频率受限，请稍后重试，也可更换出行方式或前往高德地图"
                : "地图服务暂不可用，请稍后重试，也可更换出行方式或前往高德地图", Map.copyOf(data));
    }

    public Response get(Endpoint endpoint, Map<String, String> parameters, String verifiedReferer) {
        String query = parameters.entrySet().stream().map(entry -> encode(entry.getKey()) + "=" + encode(entry.getValue()))
                .collect(Collectors.joining("&"));
        HttpRequest.Builder request = HttpRequest.newBuilder(URI.create("https://" + endpoint.host + endpoint.path + "?" + query))
                .timeout(Duration.ofSeconds(8)).header("Accept-Encoding", "identity").GET();
        if (verifiedReferer != null) request.header("Referer", verifiedReferer);
        AtomicReference<BoundedSubscriber> activeBody = new AtomicReference<>();
        AtomicBoolean aborted = new AtomicBoolean();
        CompletableFuture<HttpResponse<byte[]>> pending = null;
        try {
            pending = client.sendAsync(request.build(), info -> {
                BoundedSubscriber subscriber = new BoundedSubscriber(endpoint);
                activeBody.set(subscriber);
                if (aborted.get()) subscriber.cancel(null);
                else if (info.headers().firstValueAsLong("Content-Length").orElse(0) > MAX_BODY_BYTES) {
                    subscriber.cancel(FailureReason.UPSTREAM_FORMAT);
                }
                return subscriber;
            });
            // This future completes only when the bounded body subscriber finishes, so slow bodies share the deadline.
            HttpResponse<byte[]> response = pending.get(8, TimeUnit.SECONDS);
            if (response.statusCode() < 200 || response.statusCode() >= 300) {
                throw failure(endpoint, response.statusCode() == 429 ? FailureReason.RATE_LIMITED
                        : FailureReason.UPSTREAM_UNAVAILABLE, FailureStage.HTTP, null, null);
            }
            if (!response.headers().firstValue("Content-Encoding").orElse("identity").equalsIgnoreCase("identity")) {
                throw failure(endpoint, FailureReason.UPSTREAM_FORMAT, FailureStage.HTTP, null, null);
            }
            String contentType = response.headers().firstValue("Content-Type").orElse("application/json;charset=UTF-8");
            if (contentType.length() > 200 || contentType.contains("\r") || contentType.contains("\n")) {
                throw failure(endpoint, FailureReason.UPSTREAM_FORMAT, FailureStage.HTTP, null, null);
            }
            return new Response(response.statusCode(), contentType, response.body());
        } catch (BusinessException failed) {
            throw failed;
        } catch (TimeoutException timedOut) {
            throw failure(endpoint, FailureReason.UPSTREAM_TIMEOUT, FailureStage.HTTP, null, null);
        } catch (ExecutionException failed) {
            Throwable cause = failed.getCause();
            boolean timeout = false;
            for (int depth = 0; cause != null && depth < 8; depth++, cause = cause.getCause()) {
                if (cause instanceof BusinessException business) throw business;
                if (cause instanceof HttpTimeoutException || cause instanceof TimeoutException) timeout = true;
            }
            throw failure(endpoint, timeout ? FailureReason.UPSTREAM_TIMEOUT
                    : FailureReason.UPSTREAM_NETWORK, FailureStage.HTTP, null, null);
        } catch (InterruptedException interrupted) {
            Thread.currentThread().interrupt();
            throw failure(endpoint, FailureReason.UPSTREAM_NETWORK, FailureStage.HTTP, null, null);
        } catch (Exception failed) {
            throw failure(endpoint, FailureReason.UPSTREAM_NETWORK, FailureStage.HTTP, null, null);
        } finally {
            aborted.set(true);
            if (pending != null && !pending.isDone()) pending.cancel(true);
            BoundedSubscriber subscriber = activeBody.get();
            if (subscriber != null) subscriber.cancel(null);
        }
    }

    private static String encode(String value) { return URLEncoder.encode(value, StandardCharsets.UTF_8).replace("+", "%20"); }

    private static final class BoundedSubscriber implements HttpResponse.BodySubscriber<byte[]> {
        private final Endpoint endpoint;
        private final CompletableFuture<byte[]> result = new CompletableFuture<>();
        private final ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        private Flow.Subscription subscription;
        private boolean cancelled;

        private BoundedSubscriber(Endpoint endpoint) { this.endpoint = endpoint; }

        @Override public CompletionStage<byte[]> getBody() { return result; }
        @Override public synchronized void onSubscribe(Flow.Subscription subscription) {
            if (this.subscription != null || cancelled) { subscription.cancel(); return; }
            this.subscription = subscription;
            subscription.request(1);
        }
        @Override public synchronized void onNext(List<ByteBuffer> buffers) {
            if (cancelled) return;
            for (ByteBuffer buffer : buffers) {
                int length = buffer.remaining();
                if (length > MAX_BODY_BYTES - bytes.size()) { cancel(FailureReason.UPSTREAM_FORMAT); return; }
                byte[] chunk = new byte[length];
                buffer.get(chunk);
                bytes.writeBytes(chunk);
            }
            subscription.request(1);
        }
        @Override public synchronized void onError(Throwable error) {
            if (cancelled || result.isDone()) return;
            result.completeExceptionally(failure(endpoint, error instanceof HttpTimeoutException
                    ? FailureReason.UPSTREAM_TIMEOUT : FailureReason.UPSTREAM_NETWORK, FailureStage.HTTP, null, null));
        }
        @Override public synchronized void onComplete() { result.complete(bytes.toByteArray()); }
        private synchronized void cancel(FailureReason reason) {
            cancelled = true;
            if (subscription != null) subscription.cancel();
            if (!result.isDone()) {
                if (reason == null) result.cancel(true);
                else result.completeExceptionally(failure(endpoint, reason, FailureStage.HTTP, null, null));
            }
            bytes.reset();
        }
    }
}
