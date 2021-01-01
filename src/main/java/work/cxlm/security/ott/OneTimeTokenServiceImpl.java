package work.cxlm.security.ott;

import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import work.cxlm.cache.AbstractStringCacheStore;
import work.cxlm.utils.QfzsUtils;

import java.util.Optional;
import java.util.concurrent.TimeUnit;

/**
 * created 2020/11/19 16:26
 *
 * @author johnniang
 * @author Chiru
 */
@Service
public class OneTimeTokenServiceImpl implements OneTimeTokenService {

    private final AbstractStringCacheStore cacheStore;

    /**
     * OTT 有效天数
     */
    private static final int OTT_EXPIRE_DAY = 1;

    public OneTimeTokenServiceImpl(AbstractStringCacheStore cacheStore) {
        this.cacheStore = cacheStore;
    }

    @Override
    @NonNull
    public Optional<String> get(@NonNull String oneTimeToken) {
        Assert.hasText(oneTimeToken, "oneTimeToken 不能为空");
        return cacheStore.get(oneTimeToken);
    }

    @Override
    @NonNull
    public String create(@NonNull String uri) {
        Assert.hasText(uri, "请求链接不能为空");
        // 使用 UUID 生成 OTT
        String ott = QfzsUtils.randomUuidWithoutDash();
        cacheStore.put(ott, uri, OTT_EXPIRE_DAY, TimeUnit.DAYS);
        return ott;
    }

    @Override
    public void revoke(@NonNull String oneTimeToken) {
        Assert.hasText(oneTimeToken, "");
        cacheStore.delete(oneTimeToken);
    }
}
