package work.cxlm.service.base;

import java.util.Map;

/**
 * created 2020/12/30 9:13
 *
 * @author Chiru
 */
public interface CacheService<DOMAIN, ID> {

    /**
     * 列出全部实体
     *
     * @return 实体 ID 调用 toString 后得到的结果, 实体的映射
     */
    Map<String, DOMAIN> mapAll();

    /**
     * 跟新缓存数据
     *
     * @param cacheData 新的缓存
     */
    void updateBy(Map<String, DOMAIN> cacheData);

    /**
     * 清除缓存
     */
    void clear();

}
