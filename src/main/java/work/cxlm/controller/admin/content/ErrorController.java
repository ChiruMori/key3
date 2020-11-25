package work.cxlm.controller.admin.content;

import cn.hutool.extra.servlet.ServletUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.web.ErrorProperties;
import org.springframework.boot.autoconfigure.web.ServerProperties;
import org.springframework.boot.autoconfigure.web.servlet.error.AbstractErrorController;
import org.springframework.boot.web.error.ErrorAttributeOptions;
import org.springframework.boot.web.servlet.error.ErrorAttributes;
import org.springframework.http.HttpStatus;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.util.NestedServletException;
import work.cxlm.exception.AbstractQfzsException;
import work.cxlm.exception.NotFoundException;

import javax.servlet.http.HttpServletRequest;
import java.util.Collections;
import java.util.Map;

import static work.cxlm.model.support.QfzsConst.DEFAULT_ERROR_PATH;

/**
 * 错误页面的 Controller
 * 如果在 application 中设置了 server.error.path，就映射该值。
 * <p>
 * 如果 error.path 有值就映射该值。
 * <p>
 * 否则映射 /error。
 * created 2020/11/21 11:02
 *
 * @author ryanwang
 * @author Chiru
 */
@Slf4j
@Controller
@RequestMapping("${server.error.path:${error.path:/error}}")
public class ErrorController extends AbstractErrorController {

    private static final String COULD_NOT_RESOLVE_VIEW_WITH_NAME_PREFIX = "Could not resolve view with name '";

    private final ErrorProperties errorProperties;

    public ErrorController(ErrorAttributes errorAttributes,
                           ServerProperties serverProperties) {
        super(errorAttributes);
        this.errorProperties = serverProperties.getError();
    }

    /**
     * 错误错误页面请求
     */
    @GetMapping
    public String handleError(HttpServletRequest request, Model model) {
        log.error("请求 URL: [{}], URI: [{}], 请求方式: [{}], IP: [{}]",
                request.getRequestURL(),
                request.getRequestURI(),
                request.getMethod(),
                ServletUtil.getClientIP(request));

        handleCustomException(request);

        Map<String, Object> errorDetail = Collections.unmodifiableMap(getErrorAttributes(request,
                ErrorAttributeOptions.of(ErrorAttributeOptions.Include.STACK_TRACE)));
        model.addAttribute("error", errorDetail);
        log.debug("错误详情: [{}]", errorDetail);

        return defaultErrorHandler();
    }

    private String defaultErrorHandler() {
        return DEFAULT_ERROR_PATH;
    }

    /**
     * 处理自定义异常
     */
    private void handleCustomException(@NonNull HttpServletRequest request) {
        Assert.notNull(request, "Http servlet request must not be null");

        Object throwableObject = request.getAttribute("javax.servlet.error.exception");
        if (throwableObject == null) {
            return;
        }

        Throwable throwable = (Throwable) throwableObject;

        if (throwable instanceof NestedServletException) {
            log.error("捕获 NestedServletException", throwable);
            Throwable rootCause = ((NestedServletException) throwable).getRootCause();
            if (rootCause instanceof AbstractQfzsException) {
                AbstractQfzsException qfzsException = (AbstractQfzsException) rootCause;
                request.setAttribute("javax.servlet.error.status_code", qfzsException.getStatus().value());
                request.setAttribute("javax.servlet.error.exception", rootCause);
                request.setAttribute("javax.servlet.error.message", qfzsException.getMessage());
            }
        } else if (StringUtils.startsWithIgnoreCase(throwable.getMessage(), COULD_NOT_RESOLVE_VIEW_WITH_NAME_PREFIX)) {
            log.debug("捕获异常，无法找到 View 模板文件", throwable);
            request.setAttribute("javax.servlet.error.status_code", HttpStatus.NOT_FOUND.value());

            NotFoundException viewNotFound = new NotFoundException("该路径没有对应的模板");
            request.setAttribute("javax.servlet.error.exception", viewNotFound);
            request.setAttribute("javax.servlet.error.message", viewNotFound.getMessage());
        }

    }

    /**
     * 返回错误页面模板的路径
     */
    @Override
    public String getErrorPath() {
        return this.errorProperties.getPath();
    }

}
