package work.cxlm.security.filter;

import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.NonNull;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.util.UrlPathHelper;
import work.cxlm.cache.AbstractStringCacheStore;
import work.cxlm.config.QfzsProperties;
import work.cxlm.exception.AbstractQfzsException;
import work.cxlm.exception.BadRequestException;
import work.cxlm.exception.ForbiddenException;
import work.cxlm.model.support.QfzsConst;
import work.cxlm.security.context.SecurityContextHolder;
import work.cxlm.security.handler.AuthenticationFailureHandler;
import work.cxlm.security.handler.DefaultAuthenticationFailureHandler;
import work.cxlm.security.ott.OneTimeTokenService;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.*;

/**
 * 处理用户授权的抽象过滤器，支持 URL 的黑白名单，白名单优先级更高
 * created 2020/11/18 22:11
 *
 * @author johnniang
 * @author Chiru
 */
@Slf4j
public abstract class AbstractAuthenticationFilter extends OncePerRequestFilter {

    private final AntPathMatcher antPathMatcher;

    /**
     * URL 校验工具对象
     */
    private final UrlPathHelper urlPathHelper = new UrlPathHelper();
    private final OneTimeTokenService oneTimeTokenService;
    private final QfzsProperties qfzsProperties;
    protected final AbstractStringCacheStore cacheStore;

    /**
     * 未通过验证的错误处理
     */
    private volatile AuthenticationFailureHandler failureHandler;

    AbstractAuthenticationFilter(OneTimeTokenService oneTimeTokenService,
                                 QfzsProperties qfzsProperties,
                                 AbstractStringCacheStore cacheStore) {
        this.oneTimeTokenService = oneTimeTokenService;
        this.qfzsProperties = qfzsProperties;
        this.cacheStore = cacheStore;
        antPathMatcher = new AntPathMatcher();
    }

    /**
     * 不会被过滤器拦截并处理的 URL 集合
     */
    private Set<String> whiteUrls = new HashSet<>();

    /**
     * 会被过滤器拦截的 URL 集合，优先级低于白名单，如果请求 URL 不在黑名单则即使不在白名单也会放行
     */
    private Set<String> blackUrls = new LinkedHashSet<>();

    // --------------- Abstract -------------------------

    /**
     * 没有合法的一次性口令时，会调用本方法进行校验
     *
     * @param filterChain 过滤器链
     * @param request     Http 请求
     * @param response    Http 响应
     * @throws IOException      网络 IO 异常
     * @throws ServletException Servlet 异常
     */
    protected abstract void doAuthenticate(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws IOException, ServletException;

    /**
     * 从请求中获取登录凭证
     *
     * @param request Http 请求
     * @return 读取到的 Token
     */
    protected abstract String getTokenFromRequest(@NonNull HttpServletRequest request);

    // --------------- Override -------------------------

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response,
                                    @NonNull FilterChain filterChain) throws ServletException, IOException {
        // 校验一次性口令
        try {
            // 通过 OOT 校验，传递到过滤器链中
            if (isOneTimeAuthSufficient(request)) {
                filterChain.doFilter(request, response);
                return;
            }
            // 未通过验证
            doAuthenticate(request, response, filterChain);
        } catch (AbstractQfzsException e) {
            // 转入失败处理，给客户端处理厚的错误响应
            getFailHandler().onFailure(request, response, e);
        } finally { // 用户的授权状态置为无效
            SecurityContextHolder.clearContext();
        }
    }


    @Override
    protected boolean shouldNotFilter(@NonNull HttpServletRequest request) {
        Assert.notNull(request, "Http Request 不能为 null");

        String requestUri = urlPathHelper.getRequestUri(request);
        // 检测请求的 URL 是否在白名单内
        boolean canPass = whiteUrls.stream().anyMatch(whiteUrl -> antPathMatcher.match(whiteUrl, requestUri));
        canPass |= blackUrls.stream().noneMatch(blackUrl -> antPathMatcher.match(blackUrl, requestUri));
        return canPass;
    }

    // --------- 辅助方法 ------------

    /**
     * 判当前 request 中是否有设置一次性口令且与当前 URL 匹配
     */
    private boolean isOneTimeAuthSufficient(HttpServletRequest request) {
        // 获取请求中的传回的 ott
        final String oneTimeToken = getTokenFromRequest(request, QfzsConst.ONE_TIME_TOKEN_QUERY_NAME, QfzsConst.ONE_TIME_TOKEN_HEADER_NAME);
        // 没有 ott
        if (StringUtils.isEmpty(oneTimeToken)) {
            return false;
        }
        // 匹配 OOT 与 URL
        String allowUrl = oneTimeTokenService.get(oneTimeToken).orElseThrow(() -> new BadRequestException("无效的 Token"));
        String requestUrl = request.getRequestURI();
        if (!StringUtils.pathEquals(requestUrl, allowUrl)) {
            throw new ForbiddenException("Token 不匹配，禁止访问");
        }
        // 匹配成功后，收回 OTT
        oneTimeTokenService.revoke(oneTimeToken);
        return true;
    }

    /**
     * 从请求参数或者请求头中查询 token
     */
    protected String getTokenFromRequest(@NonNull HttpServletRequest request,
                                         @NonNull String tokenQueryName, @NonNull String tokenHeaderName) {
        Assert.notNull(request, "请求不能为 null");
        Assert.hasText(tokenQueryName, "queryName 不能为空");
        Assert.hasText(tokenHeaderName, "headerName 不能为空");

        // 从请求头中查询
        String accessKey = request.getHeader(tokenHeaderName);
        if (StringUtils.isEmpty(accessKey)) {
            // 从请求参数中查询
            accessKey = request.getParameter(tokenQueryName);
            log.debug("从请求参数中查找到 [{}: {}]", tokenQueryName, accessKey);
        } else {
            log.debug("从请求头中查找到 [{}: {}]", tokenHeaderName, accessKey);
        }
        return accessKey;
    }

    /**
     * 生成 AuthenticationFailureHandler 的实例
     */
    private AuthenticationFailureHandler getFailHandler() {
        // 双重校验锁单例：DefaultAuthenticationFailureHandler 的实例
        if (failureHandler == null) {
            synchronized (this) {
                if (failureHandler == null) {
                    DefaultAuthenticationFailureHandler defaultAuthenticationFailureHandler = new DefaultAuthenticationFailureHandler();
                    defaultAuthenticationFailureHandler.setProductEnv(qfzsProperties.isProductionEnv());
                    this.failureHandler = defaultAuthenticationFailureHandler;
                }
            }
        }
        return failureHandler;
    }

    // --------- getter, setter -----------------

    /**
     * 将 URL 添加到 Url 白名单，被过滤器忽略，优先级高于黑名单
     */
    public void addToWhiteSet(@NonNull String... whiteUrls) {
        Assert.notNull(whiteUrls, "添加到白名单的 URL 不能为 null");
        Collections.addAll(this.whiteUrls, whiteUrls);
    }

    /**
     * 将 URL 添加到 Url 黑名单，必须经过过滤器处理，优先级低于白名单
     */
    public void addToBlackSet(@NonNull String... blackUrls) {
        Assert.notNull(blackUrls, "添加到黑名单的 URL 不能为 null");
        Collections.addAll(this.blackUrls, blackUrls);
    }

    public Set<String> getWhiteUrls() {
        return whiteUrls;
    }

    public void setWhiteUrls(Collection<String> whiteUrls) {
        Assert.notNull(whiteUrls, "被添加的 Url 结合不能为 null");
        this.whiteUrls = new HashSet<>(whiteUrls);
    }

    public Set<String> getBlackUrls() {
        return blackUrls;
    }

    public void setBlackUrls(Collection<String> blackUrls) {
        Assert.notNull(blackUrls, "被添加的 Url 结合不能为 null");
        this.blackUrls = new HashSet<>(blackUrls);
    }

    public synchronized void setFailureHandler(@NonNull AuthenticationFailureHandler failureHandler) {
        Assert.notNull(failureHandler, "Authentication failure handler 不能为 null");

        this.failureHandler = failureHandler;
    }
}
