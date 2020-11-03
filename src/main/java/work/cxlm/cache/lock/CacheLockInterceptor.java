package work.cxlm.cache.lock;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.context.annotation.Configuration;
import org.springframework.lang.NonNull;
import org.springframework.util.Assert;
import work.cxlm.cache.AbstractStringCacheStore;
import work.cxlm.exception.FrequentAccessException;
import work.cxlm.exception.ServiceException;
import work.cxlm.utils.ServletUtils;

import java.lang.annotation.Annotation;

/**
 * CacheLock 注解的解释器
 * created 2020/10/30 10:36
 *
 * @author johnniang
 * @author cxlm
 */
@Slf4j
@Aspect
@Configuration
public class CacheLockInterceptor {

    private static final String CACHE_LOCK_PREFIX = "cache_lock_";

    private static final String CACHE_LOCK_VALUE = "locked";

    private final AbstractStringCacheStore cacheStore;

    public CacheLockInterceptor(AbstractStringCacheStore cacheStore) {
        this.cacheStore = cacheStore;
    }

    @Around("@annotation(work.cxlm.cache.lock.CacheLock)")
    public Object interceptCacheLock(ProceedingJoinPoint joinPoint) throws Throwable {
        // 获取方法签名
        MethodSignature methodSignature = (MethodSignature) joinPoint.getSignature();
        log.debug("加缓存锁：[{}]", methodSignature.toString());

        // 获取缓存锁（注解）
        CacheLock cacheLock = methodSignature.getMethod().getAnnotation(CacheLock.class);

        // 创建缓存锁（键)
        String cacheLockKey = buildCacheLockKey(cacheLock, joinPoint);
        log.debug("建立的 cache key: [{}]", cacheLockKey);
        try {
            Boolean cacheResult = cacheStore.putIfAbsent(cacheLockKey, CACHE_LOCK_VALUE, cacheLock.expired(), cacheLock.timeUnit());
            if (cacheResult == null) {
                throw new ServiceException("未知的缓存状态：" + cacheLockKey).setErrorData(cacheLockKey);
            }
            if (!cacheResult) {
                throw new FrequentAccessException("访问过于频繁，请稍候重试").setErrorData(cacheLockKey);
            }

            return joinPoint.proceed();
        } finally {
            if (cacheLock.autoDelete()) {
                cacheStore.delete(cacheLockKey);
                log.debug("删除缓存锁：[{}]", cacheLock);
            }
        }
    }

    private String buildCacheLockKey(@NonNull CacheLock cacheLock, @NonNull ProceedingJoinPoint joinPoint) {
        Assert.notNull(cacheLock, "缓存锁不能为 null");
        Assert.notNull(joinPoint, "切入点不能为 null");

        // 方法签名
        final MethodSignature signature = (MethodSignature) joinPoint.getSignature();

        StringBuilder cachedKeyBuilder = new StringBuilder(CACHE_LOCK_PREFIX);
        final String delimiter = cacheLock.delimiter();

        if (StringUtils.isNotBlank(cacheLock.prefix())) {
            cachedKeyBuilder.append(cacheLock.prefix());
        } else {
            cachedKeyBuilder.append(signature.getMethod().toString());
        }

        // 建立缓存锁（键）
        Annotation[][] parameterAnnotations = signature.getMethod().getParameterAnnotations();
        for (int i = 0; i < parameterAnnotations.length; i++) {
            log.debug("参数注解：[{}]: {}", i, parameterAnnotations[i]);

            for (int j = 0; j < parameterAnnotations[i].length; j++) {
                Annotation annotation = parameterAnnotations[i][j];
                log.debug("参数注解 [{}][{}]: {}", i, j, annotation);
                if (annotation instanceof CacheLock) {
                    // 获取当前参数
                    Object args = joinPoint.getArgs();
                    log.debug("Cache args: [{}]", args);
                    // 拼接缓存键
                    cachedKeyBuilder.append(delimiter).append(args.toString());
                }
            }
        }
        // 拼接 HTTP 请求 IP
        if (cacheLock.traceRequest()) {
            cachedKeyBuilder.append(delimiter).append(ServletUtils.getRequestIp());
        }
        return cachedKeyBuilder.toString();
    }
}
