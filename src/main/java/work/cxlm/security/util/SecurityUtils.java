package work.cxlm.security.util;

import org.springframework.lang.NonNull;
import org.springframework.util.Assert;
import work.cxlm.model.entity.User;

/**
 * created 2020/11/9 10:03
 *
 * @author johnniang
 * @author Chiru
 */
public class SecurityUtils {

    /**
     * Access Token 缓存前缀
     * TODO 针对这些键重构针对用户的缓存
     */
    private static final String TOKEN_ACCESS_CACHE_PREFIX = "key3.access.token.";
    private static final String ACCESS_TOKEN_CACHE_PREFIX = "key3.access_token.";

    public static final String TOKEN_REFRESH_CACHE_PREFIX = "key3.refresh.token.";
    public static final String REFRESH_TOKEN_CACHE_PREFIX = "key3.refresh_token.";

    @NonNull
    public static String buildAccessTokenKey(@NonNull User user, String keyPrefix) {
        Assert.notNull(user, "用户不能为 null");
        return keyPrefix + ACCESS_TOKEN_CACHE_PREFIX + user.getId();
    }

    @NonNull
    public static String buildAccessTokenKey(@NonNull Integer uid, String keyPrefix) {
        Assert.notNull(uid, "用户不能为 null");
        return keyPrefix + ACCESS_TOKEN_CACHE_PREFIX + uid;
    }

    @NonNull
    public static String buildAccessTokenKey(@NonNull String accessToken, String keyPrefix) {
        Assert.hasText(accessToken, "Access Token 不能为空");
        return keyPrefix + TOKEN_ACCESS_CACHE_PREFIX + accessToken;
    }

    @NonNull
    public static String buildRefreshTokenKey(@NonNull User user, String keyPrefix) {
        Assert.notNull(user, "用户不能为 null");
        return keyPrefix + REFRESH_TOKEN_CACHE_PREFIX + user.getId();
    }

    @NonNull
    public static String buildRefreshTokenKey(@NonNull Integer uid, String keyPrefix) {
        Assert.notNull(uid, "用户不能为 null");
        return keyPrefix + REFRESH_TOKEN_CACHE_PREFIX + uid;
    }

    @NonNull
    public static String buildRefreshTokenKey(@NonNull String refreshToken, String keyPrefix) {
        Assert.hasText(refreshToken, "Refresh token 不能为空");
        return keyPrefix + TOKEN_REFRESH_CACHE_PREFIX + refreshToken;
    }
}
