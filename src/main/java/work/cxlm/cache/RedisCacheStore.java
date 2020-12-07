package work.cxlm.cache;

import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.NonNull;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;
import redis.clients.jedis.HostAndPort;
import redis.clients.jedis.JedisCluster;
import redis.clients.jedis.JedisPoolConfig;
import work.cxlm.config.QfzsProperties;
import work.cxlm.utils.JsonUtils;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.util.Date;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Redis 实现的字符串缓存
 * FIXME: 并不能用
 * created 2020/11/3 23:33
 *
 * @author chaos
 * @author cxlm
 */
@Slf4j
public class RedisCacheStore extends AbstractStringCacheStore {

    private volatile static JedisCluster REDIS;

    private final Lock lock = new ReentrantLock();

    // ******** 生命周期 *********

    public RedisCacheStore(QfzsProperties qfzsProperties) {
        super.qfzsProperties = qfzsProperties;
        initRedis();
    }

    @PostConstruct
    public void initRedis() {
        if (REDIS != null) {
            return;
        }
        // 解析 Redis 集群节点
        Set<HostAndPort> nodes = new HashSet<>();
        for (String hostPort : qfzsProperties.getRedisNodes()) {
            String[] hostPortArr = hostPort.split(":");
            if (hostPortArr.length >= 1) {
                String host = hostPortArr[0];
                int port = 6379;  // 默认端口
                if (hostPortArr.length > 1) {
                    try {
                        port = Integer.parseInt(hostPortArr[1]);
                    } catch (Exception e) {
                        log.error("端口转化失败：[{}]", hostPortArr[1]);
                    }
                }
                nodes.add(new HostAndPort(host, port));
            }
        }
        if (nodes.isEmpty()) {
            nodes.add(new HostAndPort("127.0.0.1", 6379));  // 使用本机作为 Redis 服务器
        }
        // 配置
        JedisPoolConfig config = new JedisPoolConfig();
        config.setMaxIdle(2);  // 最大等待连接中的数量
        config.setMaxTotal(30);  // 最大连接数
        config.setMaxWaitMillis(5000);  // 最大等待超时时长
        // FIXME: 在完成配置后，Jedis 源码一通操作后，成功将 HOST 的值转换成了 localhost，导致无法连接
        // 疑似配置问题，因为代码本身来自 Halo 源码，Jedis 出现 bug 的可能性也不高，日后可以将 Redis 搭起来试一下
        REDIS = new JedisCluster(nodes, 5000, 2000, 3,
                qfzsProperties.getRedisPwd(), config);
        log.info("初始化 Redis 集群，[{}]", REDIS.getClusterNodes());
    }

    @PreDestroy
    public void preDestroy() {
        // REDIS.close(); // JEDIS 使用了池化技术，使用结束后无需关闭
        log.debug("关闭连接");
    }

    // ******** 方法实现 *********

    @Override
    Optional<CacheWrapper<String>> gerInternal(@NonNull String key) {
        Assert.hasText(key, "缓存键不能为空");
        String val = REDIS.get(key);
        return StringUtils.isEmpty(val) ? Optional.empty() : jsonToCacheWrapper(val);
    }

    @Override
    void putInternal(@NonNull String key, @NonNull CacheWrapper<String> value) {
        // putInternalIfAbsent(key, value);
        Assert.hasText(key, "缓存键不能为空");
        Assert.notNull(value, "缓存值不能为 null");
        try {
            REDIS.set(key, JsonUtils.objectToJson(value));
            // 设置过期时间
            Date deadline = value.getExpireAt();
            if (deadline != null) {
                REDIS.expireAt(key, deadline.getTime());
            }
        } catch (JsonProcessingException e) {
            log.warn("设置缓存失败，key: [{}], value: [{}]", key, value);
        }
    }

    @Override
    Boolean putInternalIfAbsent(@NonNull String key, @NonNull CacheWrapper<String> value) {
        Assert.hasText(key, "缓存键不能为空");
        Assert.notNull(value, "缓存值不能为 null");
        try {
            if (REDIS.setnx(key, JsonUtils.objectToJson(value)) < 0) {
                log.warn("设置缓存失败，键已存在，[{}]", key);
                return false;
            }
            // 设置过期时间
            Date deadline = value.getExpireAt();
            if (deadline != null) {
                REDIS.expireAt(key, deadline.getTime());
            }
            return true;
        } catch (JsonProcessingException e) {
            log.warn("设置缓存失败，key: [{}], value: [{}]", key, value);
            return false;
        }
    }

    @Override
    public void deleteInternal(@NonNull String key) {
        Assert.hasText(key, "缓存键不能为空");
        REDIS.del(key);
        log.debug("移除缓存键: [{}]", key);
    }
}
