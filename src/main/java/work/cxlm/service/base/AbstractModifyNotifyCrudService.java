package work.cxlm.service.base;

import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import work.cxlm.repository.BaseRepository;

import java.util.Collection;
import java.util.List;
import java.util.function.Function;

/**
 * 带有修改、增删监听机制的 CRUD Service 实现
 * created 2020/12/30 9:17
 *
 * @param <ID> ID 必须重写 toString 方法，每个 ID toString 后的结果必须唯一
 * @author Chiru
 */
@Slf4j
public abstract class AbstractModifyNotifyCrudService<DOMAIN, ID> extends AbstractCrudService<DOMAIN, ID> {

    protected final Function<DOMAIN, ID> idGetter;

    protected AbstractModifyNotifyCrudService(BaseRepository<DOMAIN, ID> repository,
                                              Function<DOMAIN, ID> idGetter) {
        super(repository);
        this.idGetter = idGetter;
    }

    @Override
    @NonNull
    public DOMAIN create(@NonNull DOMAIN domain) {
        DOMAIN created = super.create(domain);
        afterModified(created);
        return created;
    }

    @Override
    @NonNull
    public List<DOMAIN> createInBatch(@NonNull Collection<DOMAIN> domains) {
        List<DOMAIN> created = super.createInBatch(domains);
        afterModifiedBatch(domains);
        return created;
    }

    @Override
    @NonNull
    public DOMAIN update(@NonNull DOMAIN domain) {
        DOMAIN updated = super.update(domain);
        afterModified(updated);
        return updated;
    }

    @Override
    @NonNull
    public List<DOMAIN> updateInBatch(@NonNull Collection<DOMAIN> domains) {
        List<DOMAIN> updated = super.updateInBatch(domains);
        afterModifiedBatch(updated);
        return updated;
    }

    @Override
    @NonNull
    public void remove(@NonNull DOMAIN domain) {
        super.remove(domain);
        afterDeleted(idGetter.apply(domain));
    }

    @Override
    public void removeInBatch(@NonNull Collection<ID> ids) {
        super.removeInBatch(ids);
        afterDeletedBatch(ids);
    }

    @Override
    public void removeAll() {
        super.removeAll();
        afterDeletedBatch(null);
    }

    /**
     * 删除元素后调用
     *
     * @param id 实例的 ID
     */
    protected abstract void afterDeleted(@NonNull ID id);

    /**
     * 修改元素（包括新增）后调用
     *
     * @param domain 实例
     */
    protected abstract void afterModified(@NonNull DOMAIN domain);

    /**
     * 批量删除元素后调用，调用本方法时，不会调用 afterDeleted
     *
     * @param ids 要删除的元素 ID 集合。为 null 时表示全部删除
     */
    protected abstract void afterDeletedBatch(@Nullable Collection<ID> ids);

    /**
     * 批量修改元素（包括新增）后调用，调用本方法时，不会调用 afterModified
     *
     * @param domains 实例集合
     */
    protected abstract void afterModifiedBatch(@NonNull Collection<DOMAIN> domains);
}
