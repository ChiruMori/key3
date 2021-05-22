package work.cxlm.lock;

import org.springframework.core.annotation.AliasFor;

import java.lang.annotation.*;
import java.util.concurrent.TimeUnit;

/**
 * 缓存锁注解
 * created 2020/10/29 15:50
 *
 * @author johnniang
 * @author cxlm
 * @deprecated 使用 @DsLock 替代，分布式锁的实现方案
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
@Deprecated
public @interface CacheLock {

    /**
     * 缓存前缀
     */
    @AliasFor("value")
    String prefix() default "";

    /**
     * prefix 的别名
     */
    @AliasFor("prefix")
    String value() default "";

    /**
     * 使用方法的参数建立缓存键，默认为使用方法签名
     * 如果设置了值，则会使用参数，可用于与其他方法互斥
     */
    String[] argSuffix() default {};

    /**
     * 缓存过期时间，默认 5
     */
    long expired() default 5;

    /**
     * 过期时间单位，默认秒
     */
    TimeUnit timeUnit() default TimeUnit.SECONDS;

    /**
     * 定界符
     */
    String delimiter() default ":";

    /**
     * 调用方法后是否自动清除缓存
     */
    boolean autoDelete() default true;

    /**
     * 是否回溯请求信息
     */
    boolean traceRequest() default false;

    String msg() default "同时访问人数过多，请重试";
}
