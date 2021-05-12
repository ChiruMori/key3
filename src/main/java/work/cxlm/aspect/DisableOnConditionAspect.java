package work.cxlm.aspect;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;
import work.cxlm.annotation.DisableOnCondition;
import work.cxlm.config.Key3Properties;
import work.cxlm.exception.ForbiddenException;
import work.cxlm.model.enums.Mode;

/**
 * 注解 DisableApi 的切面
 * created 2020/11/15 10:34
 *
 * @author guqing
 * @author Chiru
 */
@Aspect
@Slf4j
@Component
public class DisableOnConditionAspect {

    private final Key3Properties key3Properties;

    public DisableOnConditionAspect(Key3Properties key3Properties) {
        this.key3Properties = key3Properties;
    }

    @Pointcut("@annotation(work.cxlm.annotation.DisableOnCondition)")
    public void pointcut() {
    }

    @Around("pointcut() && @annotation(disableApi)")
    public Object around(ProceedingJoinPoint joinPoint,
                         DisableOnCondition disableApi) throws Throwable {
        Mode mode = disableApi.mode();
        if (key3Properties.getMode().equals(mode)) {
            throw new ForbiddenException("禁止访问");
        }

        return joinPoint.proceed();
    }
}
