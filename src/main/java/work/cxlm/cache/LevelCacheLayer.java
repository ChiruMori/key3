package work.cxlm.cache;

import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.extern.slf4j.Slf4j;
import org.iq80.leveldb.*;
import org.iq80.leveldb.impl.Iq80DBFactory;
import org.springframework.lang.NonNull;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;
import work.cxlm.config.Key3Properties;
import work.cxlm.utils.JsonUtils;
import work.cxlm.utils.Key3DateUtils;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.*;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * Level DB 实现的字符串缓存
 * created 2020/11/1 22:50
 *
 * @author cxlm
 * @author Pencilso
 */
@Slf4j
public class LevelCacheLayer extends AbstractStringCacheLayer {

    private Key3Properties key3Properties;

    /**
     * 清理器的工作周期
     */
    public static final long PERIOD = 60;

    private static DB LEVEL_DB;

    /**
     * 执行自动清理任务的定时器
     */
    private final ScheduledExecutorService timerPool;

    //// -------------------- 生命周期 --------------------

    public LevelCacheLayer(Key3Properties properties) {
        init();
        this.key3Properties = properties;
        timerPool = new ScheduledThreadPoolExecutor(1, t -> new Thread(t, "缓存自动清除线程"));
        timerPool.scheduleAtFixedRate(new CacheExpiryCleaner(), 0, PERIOD, TimeUnit.SECONDS);
    }

    @PostConstruct
    public void init() {
        if (LEVEL_DB != null) {
            return;
        }
        try {
            // 工作路径
            File folder = new File(key3Properties.getWorkDir() + ".leveldb");
            DBFactory factory = new Iq80DBFactory();
            Options options = new Options();
            options.createIfMissing(true);

            LEVEL_DB = factory.open(folder, options);
        } catch (IOException e) {
            log.error("初始化数据库失败", e);
        }
    }

    @PreDestroy
    public void preDestroy() {
        try {
            LEVEL_DB.close();
            timerPool.shutdown();
        } catch (IOException e) {
            log.error("关闭 Level DB 出错", e);
        }
    }

    //// -------------------- 方法实现 --------------------

    /**
     * 使用默认字符集将字节数组转化为字符串
     */
    private String bytesToString(byte[] key) {
        return new String(key, Charset.defaultCharset());
    }

    /**
     * 使用默认字符集将字符串转化为字节数组
     */
    private byte[] stringToBytes(String str) {
        return str.getBytes(Charset.defaultCharset());
    }

    @Override
    Optional<CacheWrapper<String>> getInternal(@NonNull String key) {
        Assert.hasText(key, "缓存键不能为空");
        byte[] valueBytes = LEVEL_DB.get(key.getBytes());
        if (valueBytes != null) {
            String val = bytesToString(valueBytes);
            if (!StringUtils.isEmpty(val)) {
                return jsonToCacheWrapper(val);
            }
        }
        return Optional.empty();
    }

    @Override
    void putInternal(@NonNull String key, @NonNull CacheWrapper<String> value) {
        Assert.notNull(value, "缓存值不能为 null");
        Assert.notNull(key, "缓存键不能为 null");

        try {
            LEVEL_DB.put(
                    stringToBytes(key),
                    stringToBytes(JsonUtils.objectToJson(value))
            );
        } catch (JsonProcessingException e) {
            log.warn("因为键转化失败，无法插入，key: [{}], value: [{}]", key, value);
        }
    }

    @Override
    Boolean putInternalIfAbsent(@NonNull String key, @NonNull CacheWrapper<String> value) {
        Assert.hasText(key, "缓存键不能为空");

        boolean res = get(key).isPresent();
        if (!res) {
            putInternal(key, value);
        }
        return !res;
    }

    @Override
    public void deleteInternal(@NonNull String key) {
        LEVEL_DB.delete(stringToBytes(key));
        log.debug("从缓存中移除键：[{}]", key);
    }

    @Override
    public void clear() {
        for (Map.Entry<byte[], byte[]> entry : LEVEL_DB) {
            LEVEL_DB.delete(entry.getKey());
        }
    }

    //// -------------------- 定时任务，自动清除 --------------------

    private class CacheExpiryCleaner extends TimerTask {

        @Override
        public void run() {
            // 创建一组删除批处理任务
            WriteBatch deleteBatch = LEVEL_DB.createWriteBatch();

            DBIterator iterator = LEVEL_DB.iterator();
            Date now = Key3DateUtils.now();
            while (iterator.hasNext()) {
                Map.Entry<byte[], byte[]> next = iterator.next();  // Level DB 的键值均为字节数组
                if (next.getKey() == null || next.getValue() == null) {
                    continue;  // 跳过无意义的键值
                }
                String valueJson = bytesToString(next.getKey());
                if (!StringUtils.isEmpty(valueJson)) {
                    Optional<CacheWrapper<String>> wrappedValueOptional = jsonToCacheWrapper(valueJson);

                    if (wrappedValueOptional.isPresent()) {
                        Date expire = wrappedValueOptional.map(CacheWrapper::getExpireAt).orElse(null);

                        // 设置了超时，且当前时间已位于超时后的时间点，则判定为超时
                        if (expire != null && now.after(expire)) {
                            deleteBatch.delete(next.getKey());
                            log.debug("删除过期键：[{}]", bytesToString(next.getKey()));
                        }
                    }
                }
            }
            LEVEL_DB.write(deleteBatch);  // 执行任务
        }
    }
}
