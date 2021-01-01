package work.cxlm.service.base;

import org.springframework.data.domain.Sort;
import org.springframework.lang.NonNull;
import org.springframework.util.Assert;
import work.cxlm.cache.AbstractStringCacheStore;
import work.cxlm.repository.BaseRepository;
import work.cxlm.utils.ServiceUtils;

import java.lang.ref.WeakReference;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 带有缓存的 CrudService，针对分页、排序请求没有任何办法
 * created 2020/12/30 9:17
 *
 * @author Chiru
 */
public abstract class AbstractCacheCrudService<DOMAIN, ID> extends AbstractCrudService<DOMAIN, ID> implements CacheService<DOMAIN, ID> {

    private final String cacheKey;
    private final AbstractStringCacheStore cacheStore;
    private final Function<DOMAIN, ID> idGetter;
    private WeakReference<Map<ID, DOMAIN>> cachedMap;

    protected AbstractCacheCrudService(BaseRepository<DOMAIN, ID> repository,
                                       AbstractStringCacheStore cacheStore,
                                       Function<DOMAIN, ID> idGetter,
                                       String cacheKey) {
        super(repository);
        this.cacheKey = cacheKey;
        this.cacheStore = cacheStore;
        this.idGetter = idGetter;
        this.cachedMap = new WeakReference<>(null);
    }

    @Override
    @SuppressWarnings("unchecked")
    public Map<ID, DOMAIN> mapAll() {
        Map<ID, DOMAIN> weakCachedMap = cachedMap.get();
        if(weakCachedMap != null) {
            return weakCachedMap;
        }
        return cacheStore.getAny(cacheKey, Map.class).orElseGet(() -> {
            List<DOMAIN> domains = super.listAll();
            Map<ID, DOMAIN> idDomainMap = ServiceUtils.convertToMap(domains, idGetter);
            cacheStore.putAny(cacheKey, idDomainMap);
            cachedMap.clear();
            cachedMap = new WeakReference<>(idDomainMap);
            return idDomainMap;
        });
    }

    @Override
    public void updateBy(Map<ID, DOMAIN> cacheData) {
        cachedMap.clear();
        cachedMap = new WeakReference<>(cacheData);
        cacheStore.putAny(cacheKey, cacheData);
    }

    @Override
    public void clear() {
        // 先清 Redis 再清内存
        cacheStore.delete(cacheKey);
        cachedMap.clear();
    }

    @Override
    @NonNull
    public List<DOMAIN> listAll() {
        Map<ID, DOMAIN> idDomainMap = mapAll();
        return ServiceUtils.convertToList(idDomainMap);
    }

    @Override
    @NonNull
    public List<DOMAIN> listAll(@NonNull Sort sort) {
        Assert.notNull(sort, "排序参数不能为 null");
        List<DOMAIN> domains = super.listAll();
        Map<ID, DOMAIN> idDomainMap = ServiceUtils.convertToMap(domains, idGetter);
        updateBy(idDomainMap);
        return domains;
    }

    @Override
    @NonNull
    public List<DOMAIN> listAllByIds(Collection<ID> ids) {
        if (ids == null) {
            return Collections.emptyList();
        }
        Map<ID, DOMAIN> idDomainMap = mapAll();
        return ids.stream().map(idDomainMap::get).collect(Collectors.toList());
    }

    @Override
    @NonNull
    public List<DOMAIN> listAllByIds(Collection<ID> ids, @NonNull Sort sort) {
        return super.listAllByIds(ids, sort);
    }

    @Override
    @NonNull
    public Optional<DOMAIN> fetchById(@NonNull ID id) {
        return Optional.ofNullable(mapAll().get(id));
    }

    @Override
    public boolean existsById(@NonNull ID id) {
        return mapAll().containsKey(id);
    }

    @Override
    public long count() {
        return mapAll().size();
    }

    @Override
    @NonNull
    public DOMAIN create(@NonNull DOMAIN domain) {
        // 保存后请缓存
        DOMAIN saved = super.create(domain);
        clear();
        return saved;
    }

    @Override
    @NonNull
    public List<DOMAIN> createInBatch(@NonNull Collection<DOMAIN> domains) {
        List<DOMAIN> savedDomains = super.createInBatch(domains);
        clear();
        return savedDomains;
    }

    @Override
    @NonNull
    public DOMAIN update(@NonNull DOMAIN domain) {
        DOMAIN updated = super.update(domain);
        clear();
        return updated;
    }

    @Override
    @NonNull
    public List<DOMAIN> updateInBatch(@NonNull Collection<DOMAIN> domains) {
        List<DOMAIN> updatedDomains = super.updateInBatch(domains);
        clear();
        return updatedDomains;
    }

    @Override
    public void flush() {
        super.flush();
        clear();
    }

    @Override
    public void remove(@NonNull DOMAIN domain) {
        super.remove(domain);
        clear();
    }

    @Override
    public void removeInBatch(@NonNull Collection<ID> ids) {
        super.removeInBatch(ids);
        clear();
    }

    @Override
    public void removeAll(@NonNull Collection<DOMAIN> domains) {
        super.removeAll(domains);
        clear();
    }

    @Override
    public void removeAll() {
        super.removeAll();
        clear();
    }
}
