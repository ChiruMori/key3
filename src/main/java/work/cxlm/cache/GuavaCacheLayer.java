package work.cxlm.cache;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.NonNull;

import java.util.Optional;
import java.util.TimerTask;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * create 2021/4/16 19:35
 *
 * @author Chiru
 */
@Slf4j
public class GuavaCacheLayer extends AbstractStringCacheLayer {

    private static final Cache<String, Object> GUAVA_CACHE =
            CacheBuilder.newBuilder()
                    //设置cache的初始大小为10，要合理设置该值
                    .initialCapacity(10)
                    // 最大值
                    .maximumSize(10000)
                    //设置并发数为5，即同一时间最多只能有5个线程往cache执行写入操作
                    .concurrencyLevel(5)
                    // 缓存默认过期时间，Guava 不支持定制过期时间，如果需要续重新拓展
                    .expireAfterWrite(1, TimeUnit.HOURS)
                    //构建cache实例
                    .build();

    @Override
    protected Optional<CacheWrapper<String>> getInternal(@NonNull String key) {
        Object valueObj = GUAVA_CACHE.getIfPresent(key);
        if (null != valueObj) {
            return jsonToCacheWrapper(valueObj.toString());
        }
        return Optional.empty();
    }

    @Override
    void putInternal(@NonNull String key, @NonNull CacheWrapper<String> value) {
        GUAVA_CACHE.put(key, value);
    }

    @Override
    void deleteInternal(@NonNull String key) {
        GUAVA_CACHE.invalidate(key);
    }

    @Override
    Boolean putInternalIfAbsent(@NonNull String key, @NonNull CacheWrapper<String> value) {
        if (get(key).isPresent()) {
            return false;
        }
        putInternal(key, value);
        return true;
    }

    @Override
    public void clear() {
        GUAVA_CACHE.invalidateAll();
    }

}
