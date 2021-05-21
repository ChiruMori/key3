package work.cxlm.cache;

import org.springframework.data.redis.core.RedisTemplate;
import work.cxlm.exception.ServiceException;
import work.cxlm.utils.SpringContextUtils;

/**
 * create 2021/4/20 14:48
 *
 * @author Chiru
 */
public class CacheLayerBuilder {

    @SuppressWarnings("unchecked")
    public static AbstractStringCacheLayer buildStringCacheLayer(String layerName) {
        switch (layerName) {
            case "redis":
                // 如果能把这个参数也自动化了，这个 Builder 也可以省略（或者反射优化掉）
                Object template = SpringContextUtils.getBean("stringRedisTemplate");
                return new RedisCacheLayer((RedisTemplate<String, String>) template);
            case "guava":
                return new GuavaCacheLayer();
            case "caffeine":
                return new CaffeineCacheLayer();
                // TODO level, memory
            default:
                throw new ServiceException("不存在缓存的实现：" + layerName);
        }
    }
}
