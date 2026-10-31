package com.pivothub.business.map.typhoon.service.impl;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.pivothub.business.map.typhoon.client.TyphoonDataClient;
import com.pivothub.business.map.typhoon.config.TyphoonProperties;
import com.pivothub.business.map.typhoon.service.TyphoonService;
import com.pivothub.common.exception.BusinessException;
import com.pivothub.commoncore.constants.redis.BusinessRedisConstants;
import com.pivothub.commoncore.util.CacheUtil;
import com.pivothub.pojo.vo.business.map.typhoon.TyphoonDetailVo;
import com.pivothub.pojo.vo.business.map.typhoon.TyphoonListVo;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.Instant;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;

/** 使用共享短期缓存查询台风，缓存失效时合并外部请求，失败时返回有时效限制的旧数据。 */
@Service
public class TyphoonServiceImpl implements TyphoonService {
    @Autowired
    private TyphoonDataClient client;
    @Autowired
    private TyphoonProperties properties;
    @Autowired
    private CacheUtil cacheUtil;
    @Autowired
    private RedissonClient redissonClient;

    private static final Logger LOG = LoggerFactory.getLogger(TyphoonServiceImpl.class);

    @Override
    public TyphoonListVo list() {
        return cached(BusinessRedisConstants.MAP_TYPHOON_LIST_CACHE_KEY, TyphoonListVo.class, client::fetchList);
    }

    @Override
    public TyphoonDetailVo detail(String typhoonId) {
        if (typhoonId == null || !typhoonId.matches("[0-9]{1,12}")) {
            throw new BusinessException(400, "台风标识格式不正确");
        }
        TyphoonListVo available = list();
        boolean active = available.getTyphoons().stream().anyMatch(item -> typhoonId.equals(item.getId()));
        if (!active) {
            if (available.isStale()) throw new BusinessException(503, "台风列表更新延迟，请稍后重新加载");
            throw new BusinessException(404, "该台风已不在当前活跃列表中，请重新加载");
        }
        TyphoonDetailVo result = cached(BusinessRedisConstants.MAP_TYPHOON_DETAIL_CACHE_KEY_PREFIX + typhoonId,
                TyphoonDetailVo.class, () -> client.fetchDetail(typhoonId));
        if (available.isStale()) result.setStale(true);
        return result;
    }

    private <T> T cached(String key, Class<T> type, Supplier<T> loader) {
        validateConfiguration();
        JSONObject previous = read(key);
        if (fresh(previous)) return value(previous, type, false);
        if (backingOff(key)) return staleOrUnavailable(previous, type);

        RLock lock;
        boolean acquired;
        try {
            lock = redissonClient.getLock(key + BusinessRedisConstants.MAP_TYPHOON_LOCK_SUFFIX);
            acquired = lock.tryLock(0, properties.getTimeoutSeconds() + 10L, TimeUnit.SECONDS);
        } catch (InterruptedException ex) {
            Thread.currentThread().interrupt();
            throw unavailable();
        } catch (Exception ex) {
            LOG.warn("Typhoon cache lock unavailable, key={}", key, ex);
            return staleOrUnavailable(previous, type);
        }
        if (!acquired) {
            if (usable(previous)) return value(previous, type, true);
            for (int attempt = 0; attempt < 10; attempt++) {
                try { Thread.sleep(150); }
                catch (InterruptedException ex) {
                    Thread.currentThread().interrupt();
                    throw unavailable();
                }
                JSONObject shared = read(key);
                if (fresh(shared)) return value(shared, type, false);
            }
            throw new BusinessException(503, "台风数据正在更新，请稍后重试");
        }

        try {
            JSONObject shared = read(key);
            if (fresh(shared)) return value(shared, type, false);
            if (backingOff(key)) return staleOrUnavailable(shared != null ? shared : previous, type);
            T result = loader.get();
            Instant now = Instant.now();
            if (result instanceof TyphoonListVo) ((TyphoonListVo) result).setFetchedAt(now.toString());
            if (result instanceof TyphoonDetailVo) ((TyphoonDetailVo) result).setFetchedAt(now.toString());
            JSONObject envelope = new JSONObject();
            envelope.put("fetchedAt", now.toString());
            envelope.put("data", result);
            try {
                cacheUtil.setObject(key, envelope, (long) properties.getStaleSeconds(), TimeUnit.SECONDS);
            } catch (Exception ex) {
                LOG.warn("Typhoon cache write unavailable");
            }
            return result;
        } catch (Exception ex) {
            LOG.warn("Typhoon source load failed, key={}", key, ex);
            try {
                cacheUtil.set(key + BusinessRedisConstants.MAP_TYPHOON_BACKOFF_SUFFIX, "1",
                        (long) properties.getRetrySeconds(), TimeUnit.SECONDS);
            } catch (Exception ignored) { LOG.warn("Typhoon retry cache unavailable"); }
            return staleOrUnavailable(previous, type);
        } finally {
            try {
                if (lock.isHeldByCurrentThread()) lock.unlock();
            }
            catch (Exception ignored) { LOG.warn("Typhoon request lock release unavailable"); }
        }
    }

    private JSONObject read(String key) {
        try {
            String raw = cacheUtil.get(key);
            if (raw == null) return null;
            JSONObject entry = JSON.parseObject(raw);
            Instant.parse(entry.getString("fetchedAt"));
            if (entry.getJSONObject("data") == null) return null;
            return entry;
        } catch (Exception ex) {
            LOG.warn("Typhoon cache read unavailable, key={}", key, ex);
            return null;
        }
    }

    private boolean backingOff(String key) {
        try { return cacheUtil.get(key + BusinessRedisConstants.MAP_TYPHOON_BACKOFF_SUFFIX) != null; }
        catch (Exception ex) {
            LOG.warn("Typhoon retry state unavailable, key={}", key, ex);
            return true;
        }
    }

    private boolean usable(JSONObject entry) {
        if (entry == null) return false;
        long age = Duration.between(Instant.parse(entry.getString("fetchedAt")), Instant.now()).getSeconds();
        return age >= 0 && age < properties.getStaleSeconds();
    }

    private boolean fresh(JSONObject entry) {
        return usable(entry) && Duration.between(Instant.parse(entry.getString("fetchedAt")), Instant.now())
                .getSeconds() < properties.getCacheSeconds();
    }

    private <T> T value(JSONObject entry, Class<T> type, boolean stale) {
        T result = entry.getJSONObject("data").toJavaObject(type);
        if (result instanceof TyphoonListVo) ((TyphoonListVo) result).setStale(stale);
        if (result instanceof TyphoonDetailVo) ((TyphoonDetailVo) result).setStale(stale);
        return result;
    }

    private <T> T staleOrUnavailable(JSONObject entry, Class<T> type) {
        if (usable(entry)) return value(entry, type, true);
        throw unavailable();
    }

    private BusinessException unavailable() {
        return new BusinessException(503, "台风数据暂时无法获取，请稍后重试");
    }

    private void validateConfiguration() {
        if (!properties.isEnabled()) throw new BusinessException(503, "台风数据服务暂未启用");
        if (properties.getCacheSeconds() < 30 || properties.getCacheSeconds() > 3600
                || properties.getStaleSeconds() < properties.getCacheSeconds()
                || properties.getStaleSeconds() > 86400 || properties.getRetrySeconds() < 5
                || properties.getRetrySeconds() > 300 || properties.getTimeoutSeconds() < 1
                || properties.getTimeoutSeconds() > 30) {
            throw new BusinessException(503, "台风数据服务配置不可用");
        }
    }
}
