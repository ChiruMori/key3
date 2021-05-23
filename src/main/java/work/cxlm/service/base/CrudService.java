package work.cxlm.service.base;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import org.springframework.transaction.annotation.Transactional;
import work.cxlm.exception.NotFoundException;
import work.cxlm.model.entity.BaseEntity;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

/**
 * created 2020/10/21 10:59
 * 包含增删改查共同行为的 service 接口
 *
 * @param <DOMAIN> domain type
 * @param <ID>     id type
 * @author johnniang
 * @author cxlm
 */
public interface CrudService<DOMAIN, ID> {

    /**
     * 列出全部 DOMAIN
     *
     * @return 全部 Domain 的列表
     */
    @NonNull
    List<DOMAIN> listAll();

    /**
     * 按指定顺序列出全部 DOMAIN
     *
     * @param sort 排序参数
     * @return 排序后全部 Domain 的列表
     */
    @NonNull
    List<DOMAIN> listAll(@NonNull Sort sort);

    /**
     * 按指定分页列出全部 DOMAIN
     *
     * @param pageable 分页参数
     * @return 全部 Domain 的分页数据集
     */
    @NonNull
    Page<DOMAIN> listAll(@NonNull Pageable pageable);

    /**
     * 按照 ID 集合列出全部 DOMAIN
     *
     * @param ids ID 集合
     * @return 根据 ID 集合查找得到的 Domain 列表
     */
    @NonNull
    List<DOMAIN> listAllByIds(@Nullable Collection<ID> ids);

    /**
     * 按照 ID 集合列出全部 DOMAIN，并排序
     *
     * @param ids  ID 集合
     * @param sort 排序参数
     * @return 根据 ID 集合查找得到排序后的 Domain 列表
     */
    @NonNull
    List<DOMAIN> listAllByIds(@Nullable Collection<ID> ids, @NonNull Sort sort);

    /**
     * 通过 ID 查找单个 DOMAIN 的 Optional
     *
     * @param id 目标 ID
     * @return Optional 包装的 Domain
     */
    @NonNull
    Optional<DOMAIN> fetchById(@NonNull ID id);

    /**
     * 通过 ID 查找单个 DOMAIN 实例
     *
     * @param id 指定的 id
     * @return 查找到的目标实例
     * @throws NotFoundException ID 不存在时抛出
     */
    @NonNull
    DOMAIN getById(@NonNull ID id);

    /**
     * 通过 ID 查找单个 DOMAIN 实例，不存在时返回 null
     *
     * @param id 指定的 id
     * @return 查找到的目标实例
     */
    @Nullable
    DOMAIN getByIdOfNullable(@NonNull ID id);

    /**
     * 查找某个 ID 是否存在
     *
     * @param id 指定的 id
     * @return 该 ID 的实例是否存在
     */
    boolean existsById(@NonNull ID id);

    /**
     * 断言某个 ID 存在，否则抛出异常
     *
     * @param id 指定的 id
     * @throws NotFoundException ID 不存在时抛出
     */
    void mustExistById(@NonNull ID id);

    /**
     * 计数
     *
     * @return 实例的总数
     */
    long count();

    /**
     * 创建（持久化存储）一个 DOMAIN
     *
     * @param domain 要保存的实体
     * @return 保存到数据库中的实体
     */
    @NonNull
    @Transactional(rollbackFor = Exception.class)
    DOMAIN create(@NonNull DOMAIN domain);

    /**
     * 创建（持久化存储）多个 DOMAIN
     *
     * @param domains 要保存的实体集合
     * @return 保存到数据库中的实体集合
     */
    @NonNull
    @Transactional(rollbackFor = Exception.class)
    List<DOMAIN> createInBatch(@NonNull Collection<DOMAIN> domains);

    /**
     * 更新一个 DOMAIN
     *
     * @param domain 要更新的实体
     * @return 更新到数据库中的实体
     */
    @NonNull
    @Transactional(rollbackFor = Exception.class)
    DOMAIN update(@NonNull DOMAIN domain);

    /**
     * 更新多个 DOMAIN
     *
     * @param domains 要更新的实体集合
     * @return 更新到数据库中的实体集合
     */
    @NonNull
    @Transactional(rollbackFor = Exception.class)
    List<DOMAIN> updateInBatch(@NonNull Collection<DOMAIN> domains);

    /**
     * 将所有等待的修改写入数据库
     */
    void flush();

    /**
     * 删除指定 ID 的 DOMAIN
     *
     * @param id 要删除的实体 id
     * @return 被删除的实体
     * @throws NotFoundException 如果指定的 DOMAIN 不存在
     */
    @NonNull
    @Transactional(rollbackFor = Exception.class)
    DOMAIN removeById(@NonNull ID id);

    /**
     * 删除指定 ID 的 DOMAIN，如果不存在则返回 null
     *
     * @param id 要删除的实体 id
     * @return 被删除的实体
     */
    @Nullable
    @Transactional(rollbackFor = Exception.class)
    DOMAIN removeByIdOfNullable(@NonNull ID id);

    /**
     * 删除指定的 DOMAIN
     *
     * @param domain 要删除的实体
     */
    @Transactional(rollbackFor = Exception.class)
    void remove(@NonNull DOMAIN domain);

    /**
     * 删除集合中指定 ID 的 DOMAIN
     *
     * @param ids 要删除的 ID 集合
     */
    @Transactional(rollbackFor = Exception.class)
    void removeInBatch(@NonNull Collection<ID> ids);

    /**
     * 删除集合中指定的 DOMAIN
     *
     * @param domains 要删除的实体集合
     */
    @Transactional(rollbackFor = Exception.class)
    void removeAll(@NonNull Collection<DOMAIN> domains);

    /**
     * 删除全部 DOMAIN
     */
    @Transactional(rollbackFor = Exception.class)
    void removeAll();
}
