package work.cxlm.cache;

import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.NonNull;
import org.springframework.util.Assert;

import javax.annotation.PreDestroy;
import java.util.Optional;
import java.util.TimerTask;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * 内存实现的缓存
 * created 2020/11/8 22:15
 *
 * @author johnniang
 * @author Chiru
 */
@Slf4j
public class InMemoryCacheLayer extends AbstractStringCacheLayer {

    /**
     * 清理器的工作周期
     */
    private final static long PERIOD = 60;

    /**
     * 缓存实际存储的 map
     */
    private final static ConcurrentHashMap<String, CacheWrapper<String>> CACHE_CONTAINER = new ConcurrentHashMap<>();

    /**
     * 执行自动清理任务的定时器
     */
    private final ScheduledExecutorService timerPool;

    private final Lock lock = new ReentrantLock();

    public InMemoryCacheLayer() {
        timerPool = new ScheduledThreadPoolExecutor(1, t -> new Thread(t, "缓存自动清除线程"));
        timerPool.scheduleAtFixedRate(new CacheExpiryCleaner(), 0, PERIOD, TimeUnit.SECONDS);
    }

    @PreDestroy
    public void preDestroy() {
        log.debug("取消定时任务");
        timerPool.shutdown();
        CACHE_CONTAINER.clear();
    }

    @Override
    Optional<CacheWrapper<String>> getInternal(@NonNull String key) {
        Assert.hasText(key, "缓存键不能为空");

        return Optional.ofNullable(CACHE_CONTAINER.get(key));
    }

    @Override
    void putInternal(@NonNull String key, @NonNull CacheWrapper<String> value) {
        Assert.hasText(key, "缓存键不能为空");
        Assert.notNull(value, "缓存值不能为 null");

        CacheWrapper<String> cacheToPut = CACHE_CONTAINER.put(key, value);
        log.debug("已添加缓存：key: [{}], value: [{}], result: [{}]", key, value, cacheToPut);
    }

    @Override
    Boolean putInternalIfAbsent(@NonNull String key, @NonNull CacheWrapper<String> value) {
        Assert.hasText(key, "缓存键不能为空");
        Assert.notNull(value, "缓存值不能为 null");

        // 加锁范围疑似过大
        lock.lock();

        try {
            Optional<String> valueOptional = get(key);

            if (valueOptional.isPresent()) {
                log.warn("添加缓存失败，键 [{}] 已存在", key);
                return false;
            }

            putInternal(key, value);
            return true;
        } finally {
            lock.unlock();
        }
    }

    @Override
    public void deleteInternal(@NonNull String key) {
        Assert.hasText(key, "缓存键不能为空");

        CACHE_CONTAINER.remove(key);
        log.debug("移除缓存：[{}]", key);
    }

    @Override
    public void clear() {
        CACHE_CONTAINER.clear();
    }

    /**
     * Cache Cleaner
     */
    private class CacheExpiryCleaner extends TimerTask {

        @Override
        public void run() {
            CACHE_CONTAINER.keySet().forEach(k -> {
                if (InMemoryCacheLayer.this.get(k).isEmpty()) {
                    log.debug("已删除缓存：[{}]", k);
                }
            });
        }
    }
}
