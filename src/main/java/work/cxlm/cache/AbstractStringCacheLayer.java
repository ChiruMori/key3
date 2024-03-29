package work.cxlm.cache;

import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.Assert;
import work.cxlm.exception.ServiceException;
import work.cxlm.utils.JsonUtils;

import java.util.Optional;
import java.util.concurrent.TimeUnit;

/**
 * 提供一些字符串缓存的实用方法
 * create 2021/4/16 18:22
 *
 * @author Chiru
 */
@Slf4j
public abstract class AbstractStringCacheLayer extends AbstractCacheLayer<String, String> {

    @SuppressWarnings("unchecked")
    protected Optional<CacheWrapper<String>> jsonToCacheWrapper(String json) {
        Assert.hasText(json, "json 字符串不能为空");

        CacheWrapper<String> restoredCache = null;
        try {
            restoredCache = JsonUtils.jsonToObject(json, CacheWrapper.class);
        } catch (JsonProcessingException e) {
            log.debug("json 转化失败：[{}]", json);
        }
        return Optional.ofNullable(restoredCache);
    }

    /**
     * 获取缓存
     *
     * @param key  缓存键
     * @param type 值的类型
     * @return 缓存值
     */
    public <T> Optional<T> getAny(@NonNull String key, @NonNull Class<T> type) {
        Assert.notNull(key, "缓存键不能为 null");

        return get(key).map(val ->{
            try {
                return JsonUtils.jsonToObject(val, type);
            } catch (JsonProcessingException e) {
                log.error("无法将 json [{}] 转化为类型 [{}]", val, type);
                return null;
            }
        });
    }

    /**
     * 设置缓存
     *
     * @param key      缓存键
     * @param value    缓存值
     * @param timeout  保留时长
     * @param timeUnit 时间单位
     */
    public <T> void putAny(@NonNull String key, @NonNull T value, long timeout, @NonNull TimeUnit timeUnit) {
        try {
            put(key, JsonUtils.objectToJson(value), timeout, timeUnit);
        } catch (JsonProcessingException e) {
            throw new ServiceException("将对象[" + value + "] 转化为 json 失败", e);
        }
    }

    /**
     * 设置缓存，不过期
     *
     * @param key   缓存键
     * @param value 缓存值
     */
    public <T> void putAny(@NonNull String key, @NonNull T value) {
        try {
            put(key, JsonUtils.objectToJson(value));
        } catch (JsonProcessingException e) {
            throw new ServiceException("将对象[" + value + "] 转化为 json 失败", e);
        }
    }
}
