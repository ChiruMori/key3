package work.cxlm.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.NonNull;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.PathMatcher;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;
import work.cxlm.model.support.Key3Const;
import work.cxlm.utils.Key3Utils;

import javax.servlet.http.HttpServletRequest;
import java.util.HashSet;
import java.util.Set;

/**
 * 拦截器，忽略黑名单内的 URL，如果黑名单内的 URL 没有对应的资源则会得到 404
 * created 2020/11/15 13:46
 *
 * @author ryanwang
 * @author Chiru
 */
@Slf4j
public class Key3RequestMappingHandlerMapping extends RequestMappingHandlerMapping {

    private final Set<String> blackPatterns = new HashSet<>();

    private final PathMatcher pathMatcher;

    private final Key3Properties key3Properties;

    public Key3RequestMappingHandlerMapping(Key3Properties key3Properties) {
        this.key3Properties = key3Properties;
        initBlackPatterns();
        pathMatcher = new AntPathMatcher();
    }

    @Override
    protected HandlerMethod lookupHandlerMethod(@NonNull String lookupPath, @NonNull HttpServletRequest request) throws Exception {
        log.debug("验证 URL: [{}]", lookupPath);
        for (String blackPattern : blackPatterns) {
            if (this.pathMatcher.match(blackPattern, lookupPath)) {
                log.debug("跳过 [{}] 因为黑名单中存在: [{}]", lookupPath, blackPattern);
                return null;
            }
        }
        return super.lookupHandlerMethod(lookupPath, request);
    }

    private void initBlackPatterns() {
        String uploadUrlPattern = Key3Utils.ensureBoth(key3Properties.getUploadUrlPrefix(), Key3Const.URL_SEPARATOR) + "**";

        blackPatterns.add("/key3/js/**");
        blackPatterns.add("/key3/images/**");
        blackPatterns.add("/key3/css/**");
        blackPatterns.add("/key3/assets/**");
        blackPatterns.add("/csrf");
        blackPatterns.add("/swagger-ui.html");
        blackPatterns.add("/webjars/**");
        blackPatterns.add(uploadUrlPattern);
        // blackPatterns.add(adminPathPattern);
    }
}
