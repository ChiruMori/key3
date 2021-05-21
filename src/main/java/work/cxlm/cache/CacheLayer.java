package work.cxlm.cache;

import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;

import java.util.Date;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

/**
 * 缓存层，用在缓存管理中心
 * create 2021/4/16 18:04
 *
 * @param <K> 键类型
 * @param <V> 值类型
 * @author johnniang
 * @author Chiru
 */
public interface CacheLayer<K, V> {

    /**
     * 从缓存中读取值
     *
     * @param key 缓存键
     * @return 对应的值，不存在时，返回 null
     */
    @NonNull
    Optional<V> get(@NonNull K key);

    /**
     * 获取到期时间
     *
     * @param key 缓存键
     * @return 到期时间
     */
    @Nullable
    Date getExpireAt(@NonNull K key);

    /**
     * 像缓存中添加缓存项
     *
     * @param key   键
     * @param value 值
     */
    void put(@NonNull K key, @NonNull V value);

    /**
     * 添加缓存，并设置过期时间
     *
     * @param key      缓存键
     * @param value    缓存值
     * @param timeout  过期时间
     * @param timeUnit 过期时间单位
     */
    void put(@NonNull K key, @NonNull V value, long timeout, @NonNull TimeUnit timeUnit);

    /**
     * 如果该键存在则设置
     *
     * @param key      缓存键
     * @param value    缓存值
     * @param timeout  过期时间
     * @param timeUnit 过期时间单位
     * @return 该缓存键是否已存在
     */
    Boolean putIfAbsent(@NonNull K key, @NonNull V value, long timeout, @NonNull TimeUnit timeUnit);


    /**
     * 删除指定的缓存
     *
     * @param key 指定的缓存键
     */
    void delete(@NonNull K key);

    /**
     * 清除全部缓存
     */
    void clear();
}
