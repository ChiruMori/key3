package work.cxlm.security.context;

import org.springframework.lang.Nullable;
import work.cxlm.security.authentication.Authentication;

/**
 * 安全上下文
 * created 2020/11/9 9:20
 *
 * @author johnniang
 * @author Chiru
 */
public interface SecurityContext {

    /**
     * 获取当前会话上下文的授权实例
     *
     * @return 当前上下文的授权对象
     */
    @Nullable
    Authentication getAuthentication();

    /**
     * 为当前安全上下文设置授权实例
     *
     * @param authentication 要设置的授权实例
     */
    void setAuthentication(@Nullable Authentication authentication);

    /**
     * 获取当前安全上下文是否已存在合法登录凭证
     *
     * @return 当前会话上下文是否包含合法的登录验证
     */
    default boolean isAuthenticated() {
        return getAuthentication() != null;
    }
}
