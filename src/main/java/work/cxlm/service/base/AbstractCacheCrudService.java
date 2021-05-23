package work.cxlm.service.base;

import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Sort;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import work.cxlm.cache.MultiStringCache;
import work.cxlm.exception.NotFoundException;
import work.cxlm.repository.BaseRepository;
import work.cxlm.utils.ServiceUtils;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 带有缓存的 CrudService，针对分页没有任何办法
 * created 2020/12/30 9:17
 *
 * @param <ID> ID 必须重写 toString 方法，每个 ID toString 后的结果必须唯一
 * @author Chiru
 */
@Slf4j
public abstract class AbstractCacheCrudService<DOMAIN, ID> extends AbstractModifyNotifyCrudService<DOMAIN, ID> {

    private final static String COUNT_PREFIX = "count.";
    private final String cacheKey;
    private final MultiStringCache multiCache;
    private final Class<DOMAIN> domainClass;

    protected AbstractCacheCrudService(BaseRepository<DOMAIN, ID> repository,
                                       MultiStringCache multiCache,
                                       Function<DOMAIN, ID> idGetter,
                                       Class<DOMAIN> domainClass,
                                       String cacheKey) {
        super(repository, idGetter);
        this.cacheKey = cacheKey;
        this.multiCache = multiCache;
        this.domainClass = domainClass;
    }

    @Override
    @NonNull
    public List<DOMAIN> listAllByIds(Collection<ID> ids) {
        if (ids == null) {
            return Collections.emptyList();
        }
        return ids.stream()
                .map(this::getById)
                .collect(Collectors.toList());
    }

    @Override
    @NonNull
    public List<DOMAIN> listAllByIds(Collection<ID> ids, @NonNull Sort sort) {
        List<DOMAIN> domains = listAllByIds(ids);
        ServiceUtils.localSort(domains, sort);
        return domains;
    }

    @Override
    @NonNull
    public Optional<DOMAIN> fetchById(@NonNull ID id) {
        String cacheKeyToUse = cacheKey + id;
        return multiCache.getAnyWithSupplier(cacheKeyToUse, () ->
            super.fetchById(id).orElseThrow(() -> new NotFoundException("找不到对应的实体："+domainClass)), domainClass);
    }

    @Override
    public boolean existsById(@NonNull ID id) {
        String idInString = id.toString();
        Optional<String> valInCache = multiCache.get(idInString);
        if (valInCache.isEmpty()) {
            return super.existsById(id);
        }
        return true;
    }

    @Override
    public long count() {
        return multiCache.getAnyWithSupplier(buildCountCacheKey(), super::count, Long.class).orElse(-1L);
    }

    @Override
    protected void afterDeleted(@NonNull ID id) {
        multiCache.delete(cacheKey + id);
        multiCache.delete(buildCountCacheKey());
    }

    @Override
    protected void afterModified(@NonNull DOMAIN domain) {
        multiCache.putAny(cacheKey + idGetter.apply(domain), domain);
    }

    @Override
    protected void afterModifiedBatch(@NonNull Collection<DOMAIN> domains) {
        for (DOMAIN domain : domains) {
            afterModified(domain);
        }
    }

    @Override
    protected void afterDeletedBatch(@Nullable Collection<ID> ids) {
        if (null == ids) {
            multiCache.clear();
            return;
        }
        for (ID id : ids) {
            multiCache.delete(id.toString());
        }
        multiCache.delete(buildCountCacheKey());
    }

    private String buildCountCacheKey() {
        return cacheKey + COUNT_PREFIX;
    }
}
