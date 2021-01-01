package work.cxlm.repository;

import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import work.cxlm.model.entity.Option;

import java.util.Optional;

/**
 * Option 仓库，JpaSpecificationExecutor 接口用于增强查询功能（支持分页、排序操作）
 * created 2020/11/9 20:55
 *
 * @author johnniang
 * @author ryanwang
 * @author Chiru
 */
public interface OptionRepository extends BaseRepository<Option, Integer>, JpaSpecificationExecutor<Option> {
    /**
     * 通过键查找 Option
     *
     * @param key 配置项 键
     * @return Optional 包装的配置项
     */
    Optional<Option> findByKey(String key);

    /**
     * 删除指定键的 Option
     *
     * @param key 配置项 键
     */
    void deleteByKey(String key);
}
