package work.cxlm.service.base;

import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Sort;
import org.springframework.lang.NonNull;
import org.springframework.util.Assert;
import work.cxlm.cache.AbstractStringCacheStore;
import work.cxlm.repository.BaseRepository;
import work.cxlm.utils.BeanUtils;
import work.cxlm.utils.ServiceUtils;

import java.lang.ref.WeakReference;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 带有缓存的 CrudService，针对分页、排序请求没有任何办法
 * created 2020/12/30 9:17
 *
 * @param <ID> ID 必须重写 toString 方法，每个 ID toString 后的结果必须唯一
 * @author Chiru
 */
@Slf4j
public abstract class AbstractCacheCrudService<DOMAIN, ID> extends AbstractCrudService<DOMAIN, ID> implements CacheService<DOMAIN, ID> {

    private final String cacheKey;
    private final AbstractStringCacheStore cacheStore;
    private final Function<DOMAIN, ID> idGetter;
    private final Class<DOMAIN> domainClass;
    private WeakReference<Map<String, DOMAIN>> cachedMap;

    protected AbstractCacheCrudService(BaseRepository<DOMAIN, ID> repository,
                                       AbstractStringCacheStore cacheStore,
                                       Function<DOMAIN, ID> idGetter,
                                       Class<DOMAIN> domainClass,
                                       String cacheKey) {
        super(repository);
        this.cacheKey = cacheKey;
        this.cacheStore = cacheStore;
        this.domainClass = domainClass;
        this.idGetter = idGetter;
        this.cachedMap = new WeakReference<>(null);
    }

    @Override
    @SuppressWarnings("unchecked")
    public Map<String, DOMAIN> mapAll() {
        Map<String, DOMAIN> weakCachedMap = cachedMap.get();
        if (weakCachedMap != null) {
            return weakCachedMap;
        }
        Map<String, Object> rawRes = cacheStore.getAny(cacheKey, Map.class).orElseGet(() -> {
            List<DOMAIN> domains = super.listAll();
            Map<String, DOMAIN> idDomainMap = ServiceUtils.convertToMap(domains, domain -> idGetter.apply(domain).toString());
            cacheStore.putAny(cacheKey, idDomainMap);
            cachedMap.clear();
            cachedMap = new WeakReference<>(idDomainMap);
            return idDomainMap;
        });
        Map<String, DOMAIN> res = new HashMap<>(rawRes.size());
        rawRes.forEach((k, v) -> {
            if (v.getClass() == domainClass) {
                res.put(k, (DOMAIN) v);
                return;
            }
            res.put(k, BeanUtils.convertFromMap(domainClass, (Map<String, ?>) v));
        });
        return res;
    }

    @Override
    public void updateBy(Map<String, DOMAIN> cacheData) {
        cachedMap.clear();
        cachedMap = new WeakReference<>(cacheData);
        cacheStore.putAny(cacheKey, cacheData);
    }

    @Override
    public void clear() {
        // 先清 Redis 再清内存
        cacheStore.delete(cacheKey);
        cachedMap.clear();
        log.info("已清除 {} 缓存", domainClass.getName());
    }

    @Override
    @NonNull
    public List<DOMAIN> listAll() {
        Map<String, DOMAIN> idDomainMap = mapAll();
        return ServiceUtils.convertToList(idDomainMap);
    }

    @Override
    @NonNull
    public List<DOMAIN> listAll(@NonNull Sort sort) {
        Assert.notNull(sort, "排序参数不能为 null");
        List<DOMAIN> domains = super.listAll();
        Map<String, DOMAIN> idDomainMap = ServiceUtils.convertToMap(domains, domain -> idGetter.apply(domain).toString());
        updateBy(idDomainMap);
        return domains;
    }

    @Override
    @NonNull
    public List<DOMAIN> listAllByIds(Collection<ID> ids) {
        if (ids == null) {
            return Collections.emptyList();
        }
        Map<String, DOMAIN> idDomainMap = mapAll();
        return ids.stream().map(id -> idDomainMap.get(id.toString())).collect(Collectors.toList());
    }

    @Override
    @NonNull
    public List<DOMAIN> listAllByIds(Collection<ID> ids, @NonNull Sort sort) {
        return super.listAllByIds(ids, sort);
    }

    @Override
    @NonNull
    public Optional<DOMAIN> fetchById(@NonNull ID id) {
        return Optional.ofNullable(mapAll().get(id.toString()));
    }

    @Override
    public boolean existsById(@NonNull ID id) {
        return mapAll().containsKey(id.toString());
    }

    @Override
    public long count() {
        return mapAll().size();
    }

    @Override
    public void flush() {
        super.flush();
        clear();
    }

    @Override
    protected void afterModified() {
        clear();
    }
}
