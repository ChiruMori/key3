package work.cxlm.cache;

import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import work.cxlm.exception.ServiceException;

import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * create 2021/4/16 18:03
 *
 * @author Chiru
 */
@Slf4j
public class MultiStringCache extends AbstractStringCacheLayer {

    private final List<AbstractStringCacheLayer> cacheLayerList;
    private final int layerCount;

    public MultiStringCache(List<AbstractStringCacheLayer> cacheLayerList) {
        this.cacheLayerList = cacheLayerList;
        layerCount = cacheLayerList.size();
        if (0 == layerCount) {
            throw new ServiceException("配置错误：没有指定缓存层");
        }
    }

    /**
     * 从各层缓存中获取缓存值，不存在的时候返回默认值
     *
     * @param key          缓存键
     * @param defaultValue 默认值
     * @return 遍历各层缓存，得到值则返回，否则返回默认值
     */
    public String get(@NonNull String key, @Nullable String defaultValue) {
        return iterateFromUpToDown(layer -> layer.get(key)).orElse(defaultValue);
    }

    /**
     * 获取缓存，如果没有则使用 supplier 查询，并逐层写入缓存
     *
     * @param key      缓存键
     * @param supplier 缓存中没有值时，调用的查询函数
     * @return 得到的值，缓存中存在则为缓存中的值，否则为 supplier 中的值
     */
    public String getWithSupplier(@NonNull String key, Supplier<String> supplier) {
        Optional<String> gotValue = get(key);
        if (gotValue.isEmpty()) {
            String generate = supplier.get();
            put(key, generate);
            return generate;
        }
        return gotValue.get();
    }

    /**
     * 从指定缓存层获取缓存
     *
     * @param key        缓存键
     * @param layerIndex 指定缓存层的索引
     * @return 指定层指定键的缓存值，没有则返回 null
     */
    public Optional<String> getFrom(@NonNull String key, int layerIndex) {
        return cacheLayerList.get(layerIndex).get(key);
    }

    /**
     * 获取缓存，值为 V 指定的类型
     *
     * @param key        缓存键
     * @param valueClass 缓存值的 Class
     * @param <V>        缓存值的类型参数
     * @return 得到的缓存值，没有则返回 null
     */
    @Override
    public <V> Optional<V> getAny(@NonNull String key, @NonNull Class<V> valueClass) {
        return iterateFromUpToDown(layer -> layer.getAny(key, valueClass));
    }

    /**
     * 获取缓存，值为 V 指定的类型
     *
     * @param key        缓存键
     * @param valueClass 缓存值的 Class
     * @param <V>        缓存值的类型参数
     * @param supplier   缓存不存在时，调用该方法进行查询，并写入各层缓存
     * @return 得到的缓存值，没有则返回 supplier 的结果
     */
    public <V> V getAny(@NonNull String key, @NonNull Class<V> valueClass, Supplier<V> supplier) {
        Optional<V> cachedValue = getAny(key, valueClass);
        if (cachedValue.isEmpty()) {
            V supplied = supplier.get();
            putAny(key, supplied);
            return supplied;
        }
        return cachedValue.get();
    }

    /**
     * 将缓存设置到指定层，不对其它层产生影响
     *
     * @param key        缓存键
     * @param value      值
     * @param layerIndex 要设置的层编号
     */
    public void setTo(@NonNull String key, @NonNull String value, int layerIndex) {
        cacheLayerList.get(layerIndex).put(key, value);
    }

    /**
     * 清除指定层以上的全部缓存
     *
     * @param layerIndex 开始清除的层（包含）
     */
    public void clearLayer(int layerIndex) {
        for (; layerIndex < layerCount; layerIndex++) {
            cacheLayerList.get(layerIndex).clear();
        }
    }

    /**
     * 清除全部缓存
     */
    @Override
    public void clear() {
        clearLayer(0);
    }

    @Override
    Optional<CacheWrapper<String>> getInternal(@NonNull String key) {
        Optional<CacheWrapper<String>> got = iterateFromUpToDown(layer -> layer.getInternal(key));
        if (got.isEmpty()) {
            return Optional.empty();
        }
        return got;
    }

    /**
     * 将缓存设置到所有层
     */
    @Override
    void putInternal(@NonNull String key, @NonNull CacheWrapper<String> value) {
        iterateFromDownToUp(layer -> layer.putInternal(key, value));
    }

    /**
     * 删除某条缓存
     *
     * @param key 缓存键
     */
    @Override
    void deleteInternal(@NonNull String key) {
        for (AbstractStringCacheLayer kvCacheLayer : cacheLayerList) {
            kvCacheLayer.delete(key);
        }
    }

    @Override
    Boolean putInternalIfAbsent(@NonNull String key, @NonNull CacheWrapper<String> value) {
        return null;
    }

    private void iterateFromDownToUp(Consumer<AbstractStringCacheLayer> function) {
        // 越靠上编号越小，越接近数据库
        for (int i = 0; i < layerCount; i++) {
            function.accept(cacheLayerList.get(i));
        }
    }

    private <T> Optional<T> iterateFromUpToDown(Function<AbstractStringCacheLayer, Optional<T>> function) {
        // 越靠上编号越小，越接近数据库
        for (int i = layerCount - 1; i >= 0; i--) {
            Optional<T> apply = function.apply(cacheLayerList.get(i));
            if (apply.isPresent()) {
                return apply;
            }
        }
        return Optional.empty();
    }
}
