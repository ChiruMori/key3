package work.cxlm.cache;

import work.cxlm.config.Key3Properties;
import work.cxlm.exception.ServiceException;
import work.cxlm.utils.spring.SpringContextUtils;

/**
 * create 2021/4/20 14:48
 *
 * @author Chiru
 */
public class CacheLayerBuilder {

    @SuppressWarnings("unchecked")
    public static AbstractStringCacheLayer buildStringCacheLayer(String layerName) {
        switch (layerName) {
            case "guava":
                return new GuavaCacheLayer();
            case "caffeine":
                return new CaffeineCacheLayer();
            case "level":
                Object key3Properties = SpringContextUtils.getBean("key3Properties");
                return new LevelCacheLayer((Key3Properties) key3Properties);
            case "memory":
                return new InMemoryCacheLayer();
            default:
                throw new ServiceException("不存在缓存的实现：" + layerName);
        }
    }
}
