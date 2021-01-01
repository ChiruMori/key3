package work.cxlm.core;

import cn.hutool.extra.servlet.ServletUtil;
import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.multipart.MultipartFile;
import work.cxlm.utils.JsonUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Objects;

/**
 * Controller 日志的切面
 * created 2020/11/15 9:46
 *
 * @author johnniang
 * @author Chiru
 */
@Aspect
@Slf4j
@Component
public class ControllerLogAop {

    /**
     * 返回值任意类型、包名中含有 controller、任何参数的方法
     */
    @Pointcut("execution(*  *..*.controller..*.*(..))")
    public void controller() {
    }

    @Around("controller()")
    public Object aroundController(ProceedingJoinPoint joinPoint) throws Throwable {
        String className = joinPoint.getTarget().getClass().getSimpleName();
        String methodName = joinPoint.getSignature().getName();
        Object[] args = joinPoint.getArgs();

        // 请求参数
        ServletRequestAttributes requestAttributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = Objects.requireNonNull(requestAttributes).getRequest();

        printRequestLog(request, className, methodName, args);
        long start = System.currentTimeMillis();
        Object returnObj = joinPoint.proceed();
        printResponseLog(className, methodName, returnObj, System.currentTimeMillis() - start);
        return returnObj;
    }


    private void printRequestLog(HttpServletRequest request, String clazzName, String methodName, Object[] args) throws JsonProcessingException {
        log.debug("请求 URL: [{}], URI: [{}], 请求方法: [{}], IP: [{}]",
                request.getRequestURL(),
                request.getRequestURI(),
                request.getMethod(),
                ServletUtil.getClientIP(request));

        if (args == null || !log.isDebugEnabled()) {
            return;
        }

        boolean shouldNotLog = false;
        for (Object arg : args) {
            if (arg == null ||
                    arg instanceof HttpServletRequest ||
                    arg instanceof HttpServletResponse ||
                    arg instanceof MultipartFile ||
                    arg.getClass().isAssignableFrom(MultipartFile[].class)) {
                shouldNotLog = true;
                break;
            }
        }

        if (!shouldNotLog) {
            String requestBody = JsonUtils.objectToJson(args);
            log.debug("{}.{} 参数: [{}]", clazzName, methodName, requestBody);
        }
    }

    private void printResponseLog(String className, String methodName, Object returnObj, long usage) throws JsonProcessingException {
        if (log.isDebugEnabled()) {
            String returnData = "";

            if (returnObj != null) {
                if (returnObj instanceof ResponseEntity) {
                    ResponseEntity<?> responseEntity = (ResponseEntity<?>) returnObj;
                    if (responseEntity.getBody() instanceof Resource) {
                        returnData = "[ BINARY DATA ]";
                    } else {
                        returnData = toString(responseEntity.getBody());
                    }
                } else {
                    returnData = toString(returnObj);
                }

            }
            log.debug("{}.{} 结果: [{}], 耗时: [{}]ms", className, methodName, returnData, usage);
        }
    }

    @NonNull
    private String toString(@Nullable Object obj) throws JsonProcessingException {
        if (obj == null) {
            return "<NULL>";
        }
        String toString;
        if (obj.getClass().isAssignableFrom(byte[].class) && obj instanceof Resource) {
            toString = "[ BINARY DATA ]";
        } else {
            toString = JsonUtils.objectToJson(obj);
        }
        return toString;
    }

}
