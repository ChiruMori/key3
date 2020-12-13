package work.cxlm.security.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.lang3.StringUtils;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import work.cxlm.cache.AbstractStringCacheStore;
import work.cxlm.config.QfzsProperties;
import work.cxlm.exception.AuthenticationException;
import work.cxlm.model.entity.User;
import work.cxlm.model.support.QfzsConst;
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

/**
 * 针对微信小程序登录状态的过滤器
 * created 2020/11/19 19:26
 *
 * @author Chiru
 */
@Component
public class WxMiniAuthenticationFilter extends AbstractAuthenticationFilter {

    private final UserService userService;

    public WxMiniAuthenticationFilter(OneTimeTokenService oneTimeTokenService,
                                      QfzsProperties qfzsProperties,
                                      AbstractStringCacheStore cacheStore,
                                      UserService userService,
                                      ObjectMapper objectMapper) {
        super(oneTimeTokenService, qfzsProperties, cacheStore);
        this.userService = userService;
        // 针对用户相关的 API 接口进行过滤
        addToBlackSet("/key3/users/api/**", "/key3/time/api/**", "/key3/club/api/**", "/key3/notice/api/**", "/key3/room/api/**");
        // 排除用户登录、更新接口、帮助接口
        addToWhiteSet("/key3/users/api/update", "/key3/users/api/login", "/key3/users/api/refresh", "/key3/users/api/help");
        DefaultAuthenticationFailureHandler failureHandler = new DefaultAuthenticationFailureHandler();
        failureHandler.setProductEnv(qfzsProperties.isProductionEnv());
        failureHandler.setObjectMapper(objectMapper);

        setFailureHandler(failureHandler);
    }

    @Override
    protected void doAuthenticate(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws IOException, ServletException {
        String token = getTokenFromRequest(request);
        if (StringUtils.isBlank(token)) {
            throw new AuthenticationException("未登录，请登录后访问");
        }
        // 从缓存中获取 openId
        Optional<String> userOpenId = cacheStore.getAny(SecurityUtils.buildAccessTokenKey(token), String.class);
        if (!userOpenId.isPresent()) {
            throw new AuthenticationException("Token 已过期或不存在").setErrorData(token);
        }
        // 从数据库中查询，并存储到安全上下文
        User user = userService.getByOpenId(userOpenId.get());
        UserDetail userDetail = new UserDetail(user);
        SecurityContextHolder.setContext(new SecurityContextImpl(new AuthenticationImpl(userDetail)));
        filterChain.doFilter(request, response);
    }

    @Override
    protected String getTokenFromRequest(@NonNull HttpServletRequest request) {
        return getTokenFromRequest(request, QfzsConst.WX_MINI_TOKEN_QUERY_NAME, QfzsConst.WX_MINI_TOKEN_HEADER_NAME);
    }
}
