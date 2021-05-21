package work.cxlm.cache;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.Expiry;
import lombok.extern.slf4j.Slf4j;
import org.checkerframework.checker.index.qual.NonNegative;
import org.springframework.lang.NonNull;

import java.util.Date;
import java.util.Optional;

/**
 * create 2021/4/27 18:29
 *
 * @author Chiru
 */
@Slf4j
public class CaffeineCacheLayer extends AbstractStringCacheLayer {

    private static final Cache<@org.checkerframework.checker.nullness.qual.NonNull String,
            @org.checkerframework.checker.nullness.qual.NonNull CacheWrapper<String>> CAFFEINE_CACHE =
            Caffeine.newBuilder()
                    //设置cache的初始大小为10，要合理设置该值
                    .initialCapacity(10)
                    // 最大值
                    .maximumSize(10000)
                    // 非固定的过期时间
                    .expireAfter(new Expiry<String, CacheWrapper<String>>() {
                        @Override
                        public long expireAfterCreate(@NonNull String key, @NonNull CacheWrapper<String> value, long currentTime) {
                            return expireOfValue(value, currentTime);
                        }

                        @Override
                        public long expireAfterUpdate(@NonNull String key, @NonNull CacheWrapper<String> value, long currentTime, @NonNegative long currentDuration) {
                            return expireOfValue(value, currentTime);
                        }

                        @Override
                        public long expireAfterRead(@NonNull String key, @NonNull CacheWrapper<String> value, long currentTime, @NonNegative long currentDuration) {
                            return expireOfValue(value, currentTime);
                        }

                        private long expireOfValue(CacheWrapper<?> wrapper, long currentTime) {
                            Date expireAt = wrapper.getExpireAt();
                            // 未设置过期时间
                            if (null == expireAt) {
                                return Long.MAX_VALUE;
                            }
                            // 基于纳秒进行单位换算
                            return expireAt.getTime() * 1000L - currentTime;
                        }
                    })
                    //构建cache实例
                    .build();

    @Override
    Optional<CacheWrapper<String>> getInternal(@NonNull String key) {
        CacheWrapper<String> valueObj = CAFFEINE_CACHE.getIfPresent(key);
        if (null != valueObj) {
            return jsonToCacheWrapper(valueObj.getData());
        }
        return Optional.empty();
    }

    @Override
    void putInternal(@NonNull String key, @NonNull CacheWrapper<String> value) {
        CAFFEINE_CACHE.put(key, value);
    }

    @Override
    void deleteInternal(@NonNull String key) {
        CAFFEINE_CACHE.invalidate(key);
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
        CAFFEINE_CACHE.invalidateAll();
    }
}
