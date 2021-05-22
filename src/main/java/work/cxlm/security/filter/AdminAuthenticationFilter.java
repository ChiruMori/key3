package work.cxlm.security.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.lang3.StringUtils;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import work.cxlm.cache.MultiStringCache;
import work.cxlm.config.Key3Properties;
import work.cxlm.model.entity.User;
import work.cxlm.model.support.Key3Const;
import work.cxlm.security.authentication.AuthenticationImpl;
import work.cxlm.security.context.SecurityContextHolder;
import work.cxlm.security.context.SecurityContextImpl;
import work.cxlm.security.handler.DefaultAuthenticationFailureHandler;
import work.cxlm.security.ott.OneTimeTokenService;
import work.cxlm.security.support.UserDetail;
import work.cxlm.security.util.SecurityUtils;
import work.cxlm.service.UserService;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Optional;

import static work.cxlm.model.support.Key3Const.ADMIN_AUTH_KEY_PREFIX;

/**
 * 验证管理后台登录状态的过滤器
 * created 2020/11/21 17:18
 *
 * @author Chiru
 */
@Component
public class AdminAuthenticationFilter extends AbstractAuthenticationFilter {

    private final UserService userService;

    AdminAuthenticationFilter(OneTimeTokenService oneTimeTokenService,
                              Key3Properties key3Properties,
                              MultiStringCache multiCache,
                              UserService userService,
                              ObjectMapper objectMapper) {
        super(oneTimeTokenService, key3Properties, multiCache);
        this.userService = userService;
        // 针对管理员相关的 API 接口进行过滤
        addToBlackSet("/key3/admin/api/**", "/key3/admin/page/**");
        // 排除管理员登录、更新 Token 接口
        addToWhiteSet("/key3/admin/api/refresh/*", "/key3/admin/api/login", "/key3/admin/page/login");
        DefaultAuthenticationFailureHandler failureHandler = new DefaultAuthenticationFailureHandler();
        failureHandler.setProductEnv(key3Properties.isProductionEnv());
        failureHandler.setObjectMapper(objectMapper);

        setFailureHandler(failureHandler);
    }

    @Override
    protected void doAuthenticate(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws IOException, ServletException {
        String token = getTokenFromRequest(request);
        if (StringUtils.isBlank(token)) {
            response.sendRedirect("/key3/admin/page/login");
            return;
        }
        // 从缓存中获取管理员 userId
        Optional<Integer> adminId = multiCache.getAny(SecurityUtils.buildAccessTokenKey(token, ADMIN_AUTH_KEY_PREFIX), Integer.class);
        if (adminId.isEmpty()) {
            response.sendRedirect("/key3/admin/page/login");
            return;
        }
        // 从数据库中查询，并存储到安全上下文
        User admin = userService.getById(adminId.get());
        UserDetail userDetail = new UserDetail(admin);
        SecurityContextHolder.setContext(new SecurityContextImpl(new AuthenticationImpl(userDetail)));
        filterChain.doFilter(request, response);
    }

    @Override
    protected String getTokenFromRequest(@NonNull HttpServletRequest request) {
        return getTokenFromRequest(request, Key3Const.ADMIN_TOKEN_QUERY_NAME, Key3Const.ADMIN_TOKEN_HEADER_NAME);
    }
}
