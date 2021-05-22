package work.cxlm.service;

import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import work.cxlm.model.entity.User;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * created 2021/1/6 13:22
 *
 * @author Chiru
 */
public interface MonitorService {

    /**
     * 返回有日志生成的日期
     *
     * @return 日期对象列表
     */
    List<Date> datesHasLog();

    /**
     * 获取指定日期的日志内容
     *
     * @param targetDate 指定的日期
     * @return 日志内容（文本）
     */
    String getDateLog(Date targetDate);

    /**
     * 删除全部缓存，包括缓存中间件、应用缓存
     */
    void killAllCacheData();

    /**
     * 获取全部缓存
     *
     * @return 全部系统缓存（中间件缓存）的键值对
     * @param type 操作类型，包括：user-cache, system-options, locations, special
     * @param val 在类型为 user-cache 时，传递用户 id，special 时，传递 key
     */
    Map<String, String> getCachedData(@NonNull String type, @Nullable String val);

    /**
     * 设置缓存
     */
    void setCache(@NonNull String k, @NonNull String v);

    /**
     * 删除某个缓存
     *
     * @param k 缓存键
     */
    void deleteCache(String k);
}
