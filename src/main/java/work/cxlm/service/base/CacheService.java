package work.cxlm.service.base;

import java.util.Map;

/**
 * created 2020/12/30 9:13
 *
 * @deprecated mapAll 的设计过于粗暴，弃用
 * @author Chiru
 */
@Deprecated
public interface CacheService<DOMAIN, ID> {

    /**
     * 列出位于缓存中的全部实体
     *
     * @return 实体 ID 调用 toString 后得到的结果, 实体的映射
     */
    Map<String, DOMAIN> mapAll();

    /**
     * 更新缓存数据
     *
     * @param cacheData 新的缓存
     */
    void updateBy(Map<String, DOMAIN> cacheData);

    /**
     * 清除缓存
     */
    void clear();

}
