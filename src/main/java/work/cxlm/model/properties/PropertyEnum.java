package work.cxlm.model.properties;

import org.apache.commons.lang3.StringUtils;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import work.cxlm.model.enums.ValueEnum;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * created 2020/11/9 15:53
 *
 * @author johnniang
 * @author Chiru
 */
public interface PropertyEnum extends ValueEnum<String> {

    Class<?>[] SUPPORTED_CLASSES = new Class[]{
            String.class, Integer.class, Long.class, Boolean.class, Short.class, Byte.class, Double.class, Float.class
    };

    /**
     * 检测某个类是否支持转换
     */
    static boolean isSupportedType(Class<?> type) {
        if (type == null) {
            return false;
        }
        for (Class<?> supportedClass : SUPPORTED_CLASSES) {
            if (type.isAssignableFrom(supportedClass)) {
                return true;
            }
        }
        return type.isAssignableFrom(Number.class)
                || type.isAssignableFrom(Enum.class)
                || type.isAssignableFrom(ValueEnum.class);
    }

    /**
     * 将字符串转化为指定的类型
     *
     * @param value 字符串值
     * @param type  指定类的 Class 实例
     * @param <T>   指定类的泛型，仅支持基本类型的包装类（含 String，不含 char）
     */
    @SuppressWarnings("all")
    static <T> T convertTo(@NonNull String value, @NonNull Class<T> type) {
        Assert.notNull(value, "字符串值不能为 null");
        Assert.notNull(type, "目标类型不能为 null");

        for (Class<?> supportedClass : SUPPORTED_CLASSES) {
            if (type.isAssignableFrom(supportedClass)) {
                try {
                    Method valueMethod = supportedClass.getMethod("valueOf", String.class);
                    return (T) valueMethod.invoke(null, value);  // 通过反射调用 valueOf 方法
                } catch (ReflectiveOperationException e) {
                    if (type.isAssignableFrom(String.class)) {
                        return (T) value;
                    }
                    throw new UnsupportedOperationException("转化失败，试图转化为：" + supportedClass + "，得到的值为：" + value);
                }
            }
        }

        throw new UnsupportedOperationException("不支持的转化，类型：" + type.getName());
    }

    /**
     * 将字符串转化为 PropertyEnum 指定的类型，不能转化时，返回字符串本身
     */
    @SuppressWarnings("unchecked, rawtypes")
    static Object convertTo(@NonNull String value, @NonNull PropertyEnum propertyEnum) {
        Assert.notNull(propertyEnum, "property enum 不能为 null");

        if (StringUtils.isBlank(value)) {
            value = propertyEnum.defaultValue();
        }

        if (value == null) {
            return null;
        }

        try {
            if (propertyEnum.getType().isAssignableFrom(Enum.class)) {
                Class<Enum> type = (Class<Enum>) propertyEnum.getType();
                Enum<?> result = convertToEnum(value, type);
                return result != null ? result : value;
            }
            return convertTo(value, propertyEnum.getType());
        } catch (Exception e) {
            return value;
        }
    }

    static <T extends Enum<T>> T convertToEnum(@NonNull String value, @NonNull Class<T> type) {
        try {
            return Enum.valueOf(type, value.toUpperCase());
        } catch (Exception e) {
            return null;
        }
    }

    Class<?> getType();

    @Nullable
    String defaultValue();

    /**
     * 获得指定类型的默认值
     */
    @Nullable
    default <T> T defaultValue(Class<T> propertyType) {
        String defaultValue = defaultValue();
        if (defaultValue == null) {
            return null;
        }
        return PropertyEnum.convertTo(defaultValue, propertyType);
    }

    static Map<String, PropertyEnum> getValuePropertyEnumType() {
        List<Class<? extends PropertyEnum>> classes = new LinkedList<>();
        // 添加系统配置，增加配置类时需要在这里进行注册
        classes.add(EmailProperties.class);
        classes.add(PrimaryProperties.class);
        Map <String, PropertyEnum> result = new HashMap<>();
        classes.forEach(cls -> {
            PropertyEnum[] propertyEnums = cls.getEnumConstants();

            for (PropertyEnum propertyEnum : propertyEnums) {
                result.put(propertyEnum.getValue(), propertyEnum);
            }
        });
        return result;
    }
}
