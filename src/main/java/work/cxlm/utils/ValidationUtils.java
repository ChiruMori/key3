package work.cxlm.utils;

import org.hibernate.validator.internal.engine.path.PathImpl;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import org.springframework.util.CollectionUtils;
import org.springframework.validation.FieldError;

import javax.validation.*;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 用于校验实体字段合法性的工具类
 * created 2020/10/27 15:02
 *
 * @author cxlm
 */
public class ValidationUtils {

    private static volatile Validator VALIDATOR;

    private ValidationUtils() {
    }

    /**
     * 双重校验锁单例，获取校验工具实例
     */
    @NonNull
    public static Validator getValidator() {
        if (VALIDATOR == null) {
            synchronized (ValidationUtils.class) {
                if (VALIDATOR == null) {
                    VALIDATOR = Validation.buildDefaultValidatorFactory().getValidator();
                }
            }
        }

        return VALIDATOR;
    }

    /**
     * 手动校验 bean
     *
     * @param obj    需要校验的 bean
     * @param groups 校验类集合
     * @throws ConstraintViolationException 如果校验失败则抛出
     */
    public static void validate(Object obj, Class<?>... groups) {
        Validator validator = getValidator();

        if (obj instanceof Iterable) {
            validate((Iterable<?>) obj, groups);
        } else {
            final Set<ConstraintViolation<Object>> constraintViolations = validator.validate(obj, groups);
            // 含有未通过校验的字段
            if (!CollectionUtils.isEmpty(constraintViolations)) {
                throw new ConstraintViolationException(constraintViolations);
            }
        }
    }

    /**
     * 校验可迭代类型
     *
     * @param objs   待校验的可迭代实例
     * @param groups 校验分组
     */
    public static void validate(@Nullable Iterable<?> objs, @Nullable Class<?>... groups) {
        if (objs == null) {
            return;
        }

        Validator validator = getValidator();
        AtomicInteger wrappedIndex = new AtomicInteger(0);
        final Set<ConstraintViolation<?>> allViolations = new LinkedHashSet<>();
        objs.forEach(obj -> {
            int useIndex = wrappedIndex.getAndIncrement();
            // 得到分组校验结果
            Set<? extends ConstraintViolation<?>> violations = validator.validate(obj, groups);
            violations.forEach(v -> {
                // 为指定的验证设置迭代索引
                final Path path = v.getPropertyPath();
                if (path instanceof PathImpl) {
                    PathImpl path1 = (PathImpl) path;
                    path1.makeLeafNodeIterableAndSetIndex(useIndex);
                }
                allViolations.add(v);
            });
        });

        if (!CollectionUtils.isEmpty(allViolations)) {
            throw new ConstraintViolationException(allViolations);
        }
    }


    /**
     * 将字段验证错误转换为标准的map型，key:value = field:message
     *
     * @param constraintViolations constraint violations(包含错误信息)
     * @return 错误详情 map
     */
    @NonNull
    public static Map<String, String> mapWithValidError(Set<ConstraintViolation<?>> constraintViolations) {
        if (CollectionUtils.isEmpty(constraintViolations)) {
            return Collections.emptyMap();
        }

        Map<String, String> errMap = new HashMap<>(4);
        // Format the error message
        constraintViolations.forEach(constraintViolation ->
                errMap.put(constraintViolation.getPropertyPath().toString(), constraintViolation.getMessage()));
        return errMap;
    }

    /**
     * 将字段验证错误转换为标准的map型，key:value = field:message
     *
     * @param fieldErrors 字段错误组
     * @return 如果返回 null，则表示未出现错误
     */
    public static Map<String, String> mapWithFieldError(@Nullable List<FieldError> fieldErrors) {
        if (CollectionUtils.isEmpty(fieldErrors)) {
            return Collections.emptyMap();
        }

        Map<String, String> errMap = new HashMap<>(4);
        fieldErrors.forEach(filedError -> errMap.put(filedError.getField(), filedError.getDefaultMessage()));
        return errMap;
    }
}
