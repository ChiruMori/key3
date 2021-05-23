package work.cxlm.service.base;

import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;
import work.cxlm.exception.NotFoundException;
import work.cxlm.repository.BaseRepository;


import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

/**
 * CRUD Service 部分方法的默认实现虚基类
 *
 * @param <DOMAIN> domain type
 * @param <ID>     id type
 * @author johnniang
 * @author cxlm
 */
@Slf4j
public abstract class AbstractCrudService<DOMAIN, ID> implements CrudService<DOMAIN, ID> {

    /**
     * DOMAIN 的实际类名
     */
    private final String domainName;

    /**
     * 关联的 Repository 实体
     */
    private final BaseRepository<DOMAIN, ID> repository;

    protected AbstractCrudService(BaseRepository<DOMAIN, ID> repository) {
        this.repository = repository;

        // Get domain name
        @SuppressWarnings("unchecked")
        Class<DOMAIN> domainClass = (Class<DOMAIN>) fetchType(0);
        domainName = domainClass.getSimpleName();
    }

    /**
     * 获取实际泛型类型
     *
     * @param index 泛型索引
     * @return 泛型代表的实际类型
     */
    private Type fetchType(int index) {
        Assert.isTrue(index >= 0 && index <= 1, "类型索引只能为 0 或 1");

        return ((ParameterizedType) this.getClass().getGenericSuperclass()).getActualTypeArguments()[index];
    }

    /**
     * List All
     *
     * @return List
     */
    @Override
    @NonNull
    public List<DOMAIN> listAll() {
        return repository.findAll();
    }

    /**
     * List all by sort
     *
     * @param sort sort
     * @return List
     */
    @Override
    @NonNull
    public List<DOMAIN> listAll(@NonNull Sort sort) {
        Assert.notNull(sort, "Sort 实例不能为 null");

        return repository.findAll(sort);
    }

    /**
     * List all by pageable
     *
     * @param pageable pageable
     * @return Page
     */
    @Override
    @NonNull
    public Page<DOMAIN> listAll(@NonNull Pageable pageable) {
        Assert.notNull(pageable, "Pageable 实例不能为 null");

        return repository.findAll(pageable);
    }

    /**
     * List all by ids
     *
     * @param ids ids
     * @return List
     */
    @Override
    @NonNull
    public List<DOMAIN> listAllByIds(@Nullable Collection<ID> ids) {
        return CollectionUtils.isEmpty(ids) ? Collections.emptyList() : repository.findAllById(ids);
    }

    /**
     * List all by ids and sort
     *
     * @param ids  ids
     * @param sort sort
     * @return List
     */
    @Override
    @NonNull
    public List<DOMAIN> listAllByIds(Collection<ID> ids, @NonNull Sort sort) {
        Assert.notNull(sort, "Sort 实例不能为 null");

        return CollectionUtils.isEmpty(ids) ? Collections.emptyList() : repository.findAllByIdIn(ids, sort);
    }

    /**
     * Fetch by id
     *
     * @param id id
     * @return Optional
     */
    @Override
    @NonNull
    public Optional<DOMAIN> fetchById(@NonNull ID id) {
        Assert.notNull(id, domainName + " id 不能为 null");

        return repository.findById(id);
    }

    /**
     * Get by id
     *
     * @param id id
     * @return DOMAIN
     * @throws NotFoundException If the specified id does not exist
     */
    @Override
    @NonNull
    public DOMAIN getById(@NonNull ID id) {
        return fetchById(id).orElseThrow(() -> new NotFoundException(domainName + " 不存在或已删除"));
    }

    /**
     * Gets domain of nullable by id.
     *
     * @param id id
     * @return DOMAIN
     */
    @Override
    public DOMAIN getByIdOfNullable(@NonNull ID id) {
        return fetchById(id).orElse(null);
    }

    /**
     * Exists by id.
     *
     * @param id id
     * @return boolean
     */
    @Override
    public boolean existsById(@NonNull ID id) {
        Assert.notNull(id, domainName + " id 不能为 null");

        return repository.existsById(id);
    }

    /**
     * Must exist by id, or throw NotFoundException.
     *
     * @param id id
     * @throws NotFoundException If the specified id does not exist
     */
    @Override
    public void mustExistById(@NonNull ID id) {
        if (!existsById(id)) {
            throw new NotFoundException(domainName + " 不存在");
        }
    }

    /**
     * count all
     *
     * @return long
     */
    @Override
    public long count() {
        return repository.count();
    }

    /**
     * save by domain
     *
     * @param domain domain
     * @return DOMAIN
     */
    @Override
    @NonNull
    public DOMAIN create(@NonNull DOMAIN domain) {
        Assert.notNull(domain, domainName + " 不能为 null");

        return repository.save(domain);
    }

    /**
     * save by domains
     *
     * @param domains domains
     * @return List
     */
    @Override
    @NonNull
    public List<DOMAIN> createInBatch(@NonNull Collection<DOMAIN> domains) {
        if (CollectionUtils.isEmpty(domains)) {
            return Collections.emptyList();
        }
        return repository.saveAll(domains);
    }

    /**
     * Updates by domain
     *
     * @param domain domain
     * @return DOMAIN
     */
    @Override
    @NonNull
    public DOMAIN update(@NonNull DOMAIN domain) {
        Assert.notNull(domain, domainName + " 不能为 null");

        return repository.saveAndFlush(domain);
    }

    @Override
    public void flush() {
        repository.flush();
    }

    /**
     * Updates by domains
     *
     * @param domains domains
     * @return List
     */
    @Override
    @NonNull
    public List<DOMAIN> updateInBatch(@NonNull Collection<DOMAIN> domains) {
        if (CollectionUtils.isEmpty(domains)){
            return Collections.emptyList();
        }
        return repository.saveAll(domains);
    }

    /**
     * Removes by id
     *
     * @param id id
     * @return DOMAIN
     * @throws NotFoundException If the specified id does not exist
     */
    @Override
    @NonNull
    public DOMAIN removeById(@NonNull ID id) {
        // Get non null domain by id
        DOMAIN domain = getById(id);

        // Remove it
        remove(domain);

        // return the deleted domain
        return domain;
    }

    /**
     * Removes by id if present.
     *
     * @param id id
     * @return DOMAIN
     */
    @Override
    public DOMAIN removeByIdOfNullable(@NonNull ID id) {
        return fetchById(id).map(domain -> {
            remove(domain);
            return domain;
        }).orElse(null);
    }

    /**
     * Remove by domain
     *
     * @param domain domain
     */
    @Override
    public void remove(@NonNull DOMAIN domain) {
        Assert.notNull(domain, domainName + " 不能为 null");

        repository.delete(domain);
    }

    /**
     * Remove by ids
     *
     * @param ids ids
     */
    @Override
    public void removeInBatch(@NonNull Collection<ID> ids) {
        if (CollectionUtils.isEmpty(ids)) {
            log.debug(domainName + " id 集合为空");
            return;
        }

        repository.deleteByIdIn(ids);
    }

    /**
     * Remove all by domains
     *
     * @param domains domains
     */
    @Override
    public void removeAll(@NonNull Collection<DOMAIN> domains) {
        if (CollectionUtils.isEmpty(domains)) {
            log.debug(domainName + " 集合为空");
            return;
        }
        repository.deleteInBatch(domains);
    }

    /**
     * Remove all
     */
    @Override
    public void removeAll() {
        repository.deleteAll();
    }

}
