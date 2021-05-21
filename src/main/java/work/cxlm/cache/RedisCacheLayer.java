package work.cxlm.cache;

import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.lang.NonNull;
import work.cxlm.utils.JsonUtils;

import java.util.Optional;
import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 * create 2021/4/16 19:36
 *
 * @author Chiru
 */
@Slf4j
public class RedisCacheLayer extends AbstractStringCacheLayer {

    private final RedisTemplate<String, String> template;

    public RedisCacheLayer(RedisTemplate<String, String> template) {
        this.template = template;
    }

    @Override
    Optional<CacheWrapper<String>> getInternal(@NonNull String key) {
        ValueOperations<String, String> redisOperation = template.opsForValue();
        String stringValue = redisOperation.get(key);
        if (null != stringValue) {
            return jsonToCacheWrapper(stringValue);
        }
        return Optional.empty();
    }

    @Override
    void putInternal(@NonNull String key, @NonNull CacheWrapper<String> value) {
        ValueOperations<String, String> redisOperation = template.opsForValue();
        try {
            redisOperation.set(key, JsonUtils.objectToJson(value));
        } catch (JsonProcessingException e) {
            log.debug("json 转化失败：[{}]", value);
        }
    }

    @Override
    void deleteInternal(@NonNull String key) {
        template.delete(key);
    }

    @Override
    Boolean putInternalIfAbsent(@NonNull String key, @NonNull CacheWrapper<String> value) {
        ValueOperations<String, String> redisOperation = template.opsForValue();
        try {
            if (value.getExpireAt() == null) {
                return redisOperation.setIfAbsent(key, JsonUtils.objectToJson(value));
            }
            long timeout = value.getExpireAt().getTime() - System.currentTimeMillis();
            return redisOperation.setIfAbsent(key, JsonUtils.objectToJson(value), timeout, TimeUnit.MILLISECONDS);
        } catch (JsonProcessingException e) {
            log.error("JSON 转化失败");
            return false;
        }
    }

    @Override
    public void clear() {
        Set<String> allKeys = template.keys("*");
        if (null != allKeys) {
            template.delete(allKeys);
        }
    }
}
