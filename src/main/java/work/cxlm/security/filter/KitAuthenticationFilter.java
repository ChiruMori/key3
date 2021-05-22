package work.cxlm.security.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import work.cxlm.cache.MultiStringCache;
import work.cxlm.config.Key3Properties;
import work.cxlm.exception.ForbiddenException;
import work.cxlm.model.entity.User;
import work.cxlm.security.authentication.AuthenticationImpl;
import work.cxlm.security.context.SecurityContextHolder;
import work.cxlm.security.context.SecurityContextImpl;
import work.cxlm.security.handler.DefaultAuthenticationFailureHandler;
import work.cxlm.security.ott.OneTimeTokenService;
import work.cxlm.security.support.UserDetail;
import work.cxlm.service.UserService;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * 针对救急工具包的过滤器
 * created 2020/11/19 19:26
 *
 * @author Chiru
 */
@Component
public class KitAuthenticationFilter extends AbstractAuthenticationFilter {

    private final UserService userService;

    public KitAuthenticationFilter(OneTimeTokenService oneTimeTokenService,
                                   Key3Properties key3Properties,
                                   MultiStringCache multiCache,
                                   UserService userService,
                                   ObjectMapper objectMapper) {
        super(oneTimeTokenService, key3Properties, multiCache);
        this.userService = userService;
        // 拦截救急工具包相关请求
        addToBlackSet("/key3/kit/**");
        DefaultAuthenticationFailureHandler failureHandler = new DefaultAuthenticationFailureHandler();
        failureHandler.setProductEnv(key3Properties.isProductionEnv());
        failureHandler.setObjectMapper(objectMapper);

        setFailureHandler(failureHandler);
    }

    @Override
    protected void doAuthenticate(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws IOException, ServletException {
        User admin = getUserFromRequest(request);
        // 存储到安全上下文
        UserDetail userDetail = new UserDetail(admin);
        SecurityContextHolder.setContext(new SecurityContextImpl(new AuthenticationImpl(userDetail)));
        filterChain.doFilter(request, response);
    }

    @Override
    protected String getTokenFromRequest(@NonNull HttpServletRequest request) {
        return null;
    }

    private User getUserFromRequest(@NonNull HttpServletRequest request) {
        String uidFromRequest = request.getParameter("uid");
        String openId = request.getParameter("openId");
        int uid;
        try {
            uid = Integer.parseInt(uidFromRequest);
        } catch (NumberFormatException e) {
            throw new ForbiddenException("别玩了，这东西(" + uidFromRequest + ")不是数字。");
        }
        User byOpenId = userService.getByOpenId(openId);
        if (!byOpenId.getId().equals(uid)) {
            throw new ForbiddenException("可长点心吧，用户与 wxId 不匹配。");
        }
        if (!byOpenId.getRole().isSystemAdmin()) {
            throw new ForbiddenException("该功能只有系统管理员可以使用");
        }
        return byOpenId;
    }
}
