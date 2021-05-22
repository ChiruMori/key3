package work.cxlm.security.filter;

import lombok.extern.slf4j.Slf4j;
import org.springframework.core.annotation.Order;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;
import work.cxlm.cache.MultiStringCache;
import work.cxlm.config.Key3Properties;
import work.cxlm.security.ott.OneTimeTokenService;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * 对公开内容放行的过滤器
 * created 2020/11/19 19:08
 *
 * @author Chiru
 */
@Component
@Slf4j
@Order(-1)
public class ContentFilter extends AbstractAuthenticationFilter {


    public ContentFilter(OneTimeTokenService oneTimeTokenService,
                         Key3Properties key3Properties,
                         MultiStringCache multiCache) {
        super(oneTimeTokenService, key3Properties, multiCache);

        addToBlackSet("/**");
        // 静态资源放行
        addToWhiteSet("/key3/js/**", "/key3/css/**", "/key3/vendor/**");
        // setFailureHandler(); 只在需要安装时启用，抛出异常后，转发到安装的 URL
    }

    @Override
    protected void doAuthenticate(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) {
        // 无需任何验证，直接扔到过滤器链
        try {
            filterChain.doFilter(request, response);
        } catch (ServletException | IOException e) {
            log.error(e.getMessage());
            response.setStatus(404);
        }
    }

    @Override
    protected String getTokenFromRequest(@Nullable HttpServletRequest request) {
        return null;
    }
}
