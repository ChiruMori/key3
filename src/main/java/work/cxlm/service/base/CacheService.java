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
     * @return 实体 ID, 实体的映射
     */
    Map<ID, DOMAIN> mapAll();

    /**
     * 跟新缓存数据
     *
     * @param cacheData 新的缓存
     */
    void updateBy(Map<ID, DOMAIN> cacheData);

    /**
     * 清除缓存
     */
    void clear();

}
