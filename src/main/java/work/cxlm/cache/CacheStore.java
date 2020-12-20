package work.cxlm.cache;

import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;

import java.util.Date;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

/**
 * 缓存存储接口
 * created 2020/11/1 15:01
 *
 * @param <K> 键类型
 * @param <V> 值类型
 * @author johnniang
 * @author cxlm
 */
public interface CacheStore<K, V> {

    /**
     * 通过键查找缓存
     * @param key 键
     * @return Optional 包装的值
     */
    @NonNull
    Optional<V> get(@NonNull K key);

    @Nullable
    Date getExpireAt(@NonNull K key);

    /**
     * 添加缓存，并设置过期时间
     * @param key 缓存键
     * @param value 缓存值
     * @param timeout 过期时间
     * @param timeUnit 过期时间单位
     */
    void put(@NonNull K key, @NonNull V value, long timeout, @NonNull TimeUnit timeUnit);

    /**
     * 如果该键存在则设置
     * @param key 缓存键
     * @param value 缓存值
     * @param timeout 过期时间
     * @param timeUnit 过期时间单位
     * @return 该缓存键是否已存在
     */
    Boolean putIfAbsent(@NonNull K key, @NonNull V value, long timeout, @NonNull TimeUnit timeUnit);

    /**
     * 设置一个不会过期的缓存
     * @param key 缓存键
     * @param value 缓存值
     */
    void put(@NonNull K key, @NonNull V value);

    /**
     * 删除一个缓存
     * @param key 缓存键
     */
    void delete(@NonNull K key);

}
