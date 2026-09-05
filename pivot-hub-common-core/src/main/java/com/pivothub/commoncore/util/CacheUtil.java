package com.pivothub.commoncore.util;

import cn.hutool.json.JSONUtil;
import jakarta.annotation.Resource;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.stream.*;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * @ClassName: CacheUtil
 * @Description: Redis缓存工具类
 * @Author: lhb
 */
@Component
public class CacheUtil {
    @Resource
    private StringRedisTemplate stringRedisTemplate;

    @Resource
    private RedisConnectionFactory redisConnectionFactory;

    public void setHash(String key, Map<String, Object> userMap, Long time, TimeUnit unit) {
        stringRedisTemplate.opsForHash().putAll(key, userMap);
        stringRedisTemplate.expire(key, time, unit);
    }

    public Map<Object, Object> getHash(String key) {
        return stringRedisTemplate.opsForHash().entries(key);
    }

    public void expire(String key, Long time, TimeUnit unit) {
        stringRedisTemplate.expire(key, time, unit);
    }

    public boolean deleteIfExists(String key) {
        return Boolean.TRUE.equals(stringRedisTemplate.delete(key));
    }

    public boolean deleteKeySafely(String key) {
        Boolean exists = stringRedisTemplate.hasKey(key);
        if (!exists) return true;
        return Boolean.TRUE.equals(stringRedisTemplate.delete(key));
    }

    public void updateHash(String key, String data, String field) {
    }

    public void set(String key, String value, Long time, TimeUnit unit) {
        stringRedisTemplate.opsForValue().set(key, JSONUtil.toJsonStr(value), time, unit);
    }

    public void setNoTtl(String key, String value) {
        stringRedisTemplate.opsForValue().set(key, JSONUtil.toJsonStr(value));
    }

    public Long increment(String key) {
        return stringRedisTemplate.opsForValue().increment(key);
    }

    public String get(String key) {
        String value = stringRedisTemplate.opsForValue().get(key);
        if (StringUtils.isNotBlank(value)) return value;
        return null;
    }

    public List<String> multiGet(List<String> keys) {
        return stringRedisTemplate.opsForValue().multiGet(keys);
    }

    public void setZSet(String key, String member, double score) {
        stringRedisTemplate.opsForZSet().add(key, member, score);
    }

    public void setSet(String key, String... values) {
        stringRedisTemplate.opsForSet().add(key, values);
    }

    public void setSet(String key, Collection<String> values) {
        stringRedisTemplate.opsForSet().add(key, values.toArray(new String[0]));
    }

    public boolean isMemberInSet(String key, String member) {
        return stringRedisTemplate.opsForSet().isMember(key, member);
    }

    public Set<String> getAllMembersFromSet(String key) {
        return stringRedisTemplate.opsForSet().members(key);
    }

    public void removeMember(String key, String member) {
        stringRedisTemplate.opsForZSet().remove(key, member);
    }

    public void removeMembers(String key, String... members) {
        stringRedisTemplate.opsForZSet().remove(key, members);
    }

    public Set<String> getMembersByRank(String key, long start, long end) {
        return stringRedisTemplate.opsForZSet().range(key, start, end);
    }

    public boolean isMemberInZSet(String key, String member) {
        Double score = stringRedisTemplate.opsForZSet().score(key, member);
        return score != null;
    }

    public Set<String> getMembersByReverseRank(String key, long start, long end) {
        return stringRedisTemplate.opsForZSet().reverseRange(key, start, end);
    }

    public void putHash(String key, String hashKey, String value) {
        stringRedisTemplate.opsForHash().put(key, hashKey, value);
    }

    public Long removeZSetRangeByScore(String key, double minScore, double maxScore) {
        return stringRedisTemplate.opsForZSet().removeRangeByScore(key, minScore, maxScore);
    }

    public Long getZSetSize(String key) {
        return stringRedisTemplate.opsForZSet().zCard(key);
    }

    public void addStreamRecord(String streamKey, Map<String, String> record) {
        stringRedisTemplate.opsForStream().add(streamKey, record);
    }

    public void createStreamGroup(String streamKey, String groupName) {
        try {
            Boolean streamExists = stringRedisTemplate.hasKey(streamKey);
            if (Boolean.FALSE.equals(streamExists)) {
                stringRedisTemplate.opsForStream().add(streamKey,
                        Collections.singletonMap("init", "stream_init"));
            }
            if (isConsumerGroupExists(streamKey, groupName)) {
                return;
            }
            stringRedisTemplate.opsForStream().createGroup(
                    streamKey,
                    ReadOffset.from("0"),
                    groupName
            );
        } catch (Exception e) {
            throw new RuntimeException("创建Stream消费者组异常", e);
        }
    }

    public List<MapRecord<String, Object, Object>> readStreamMessages(
            String streamKey, String groupName, String consumerName, int count) {
        Consumer consumer = Consumer.from(groupName, consumerName);
        StreamReadOptions options = StreamReadOptions.empty().count(count).block(Duration.ofSeconds(1));
        return stringRedisTemplate.opsForStream().read(consumer, options,
                StreamOffset.create(streamKey, ReadOffset.lastConsumed()));
    }

    public void acknowledgeStreamMessage(String streamKey, String groupName, String... messageIds) {
        stringRedisTemplate.opsForStream().acknowledge(groupName, streamKey, messageIds);
    }

    public boolean isConsumerGroupExists(String streamKey, String groupName) {
        try (RedisConnection connection = redisConnectionFactory.getConnection()) {
            StreamInfo.XInfoGroups groups = connection.streamCommands().xInfoGroups(streamKey.getBytes());
            return groups.stream().anyMatch(group -> group.groupName().equals(groupName));
        }
    }
}
