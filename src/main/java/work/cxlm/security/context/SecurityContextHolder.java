package work.cxlm.security.context;

import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import work.cxlm.exception.ForbiddenException;
import work.cxlm.model.entity.User;
import work.cxlm.security.authentication.Authentication;

import java.util.Optional;

/**
 * created 2020/11/15 14:14
 *
 * @author johnniang
 * @author Chiru
 */
public class SecurityContextHolder {

    private final static ThreadLocal<SecurityContext> CONTEXT_HOLDER = new ThreadLocal<>();

    private SecurityContextHolder() {
    }

    public static SecurityContext getContext() {
        SecurityContext context = CONTEXT_HOLDER.get();
        if (context == null) {
            context = createEmptyContext();
            CONTEXT_HOLDER.set(context);
        }
        return context;
    }

    public static void setContext(@Nullable SecurityContext context) {
        CONTEXT_HOLDER.set(context);
    }

    public static void clearContext() {
        CONTEXT_HOLDER.remove();
    }

    private static SecurityContext createEmptyContext() {
        return new SecurityContextImpl(null);
    }

    /**
     * 获得当前 ThreadLocal 中的用户，即当前线程中活跃的用户
     */
    public static Optional<User> getCurrentUser() {
        if (!getContext().isAuthenticated()) {
            return Optional.empty();
        }
        Authentication a = getContext().getAuthentication();
        Assert.notNull(a, "从合法上下文解析出非法登录凭证");
        return Optional.of(a.getUserDetail().getUser());
    }

    /**
     * 确保用户已登录并得到用户实体，否则抛出异常
     */
    public static User ensureUser() {
        return getCurrentUser().orElseThrow(() -> new ForbiddenException("未登录"));
    }

    /**
     * 确保是系统管理员在操作
     */
    public static User ensureSystemAdmin() {
        User admin = ensureUser();
        if (!admin.getRole().isSystemAdmin()) {
            throw new ForbiddenException("该操作需要系统管理员权限，您的权限不足");
        }
        return admin;
    }
}
