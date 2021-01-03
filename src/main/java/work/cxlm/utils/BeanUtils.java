package work.cxlm.utils;

import jdk.nashorn.internal.runtime.regexp.joni.exception.InternalException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.beanutils.ConvertUtils;
import org.apache.commons.beanutils.Converter;
import org.apache.commons.beanutils.converters.*;
import org.springframework.beans.BeanWrapperImpl;
import org.springframework.beans.BeansException;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import work.cxlm.exception.BeanUtilsException;
import work.cxlm.model.enums.UserGender;
import work.cxlm.model.enums.UserRole;

import java.beans.PropertyDescriptor;
import java.lang.reflect.InvocationTargetException;
import java.math.BigDecimal;
import java.util.*;
import java.util.function.Function;

/**
 * Bean 工具类，静态类
 * created 2020/10/22 13:49
 *
 * @author johnniang
 * @author cxlm
 */
@Slf4j
public class BeanUtils {

    static {
        // 注册转换器，处理 Null 值
        ConvertUtils.register(new LongConverter(null), Long.class);
        ConvertUtils.register(new ShortConverter(null), Short.class);
        ConvertUtils.register(new IntegerConverter(null), Integer.class);
        ConvertUtils.register(new DoubleConverter(null), Double.class);
        ConvertUtils.register(new BigDecimalConverter(null), BigDecimal.class);
        ConvertUtils.register(new DateConverter(null), Date.class);
        // User 属性：枚举类转换器
        ConvertUtils.register(new Converter() {
            @Override
            @SuppressWarnings("unchecked")
            public <T> T convert(Class<T> type, Object value) {
                if (value == null) {
                    return (T) UserGender.UNKNOWN;
                }
                return (T) UserGender.valueOf((String) value);
            }
        }, UserGender.class);
        ConvertUtils.register(new Converter() {
            @Override
            @SuppressWarnings("unchecked")
            public <T> T convert(Class<T> type, Object value) {
                if (value == null) {
                    return (T) UserRole.NORMAL;
                }
                return (T) UserRole.valueOf((String) value);
            }
        }, UserRole.class);
    }

    private BeanUtils() {
    }

    /**
     * 从当前对象生成目标类型的对象，仅拷贝属性
     *
     * @param source      源对象
     * @param targetClass 目标类对象，不能为 null
     * @param <T>         目标类类型参数
     * @return 从源对象生成的实例，如果源对象为 null 则返回 null
     * @throws BeanUtilsException 创建过程中遇到错误时抛出
     */
    @Nullable
    public static <T> T transformFrom(@Nullable Object source, @NonNull Class<T> targetClass) {
        Assert.notNull(targetClass, "目标类对象不能为 null");

        if (source == null) {
            return null;
        }

        // 构建实例
        try {
            // 从目标类对象创建一个实例
            T targetInstance = targetClass.getDeclaredConstructor().newInstance();
            // 拷贝属性
            org.springframework.beans.BeanUtils.copyProperties(source, targetInstance, getNullPropertyNames(source));
            // 返回构造的实例
            return targetInstance;
        } catch (Exception e) {
            throw new BeanUtilsException("创建 " + targetClass.getName() + " 的实例或拷贝属性失败", e);
        }
    }

    /**
     * 更新属性
     *
     * @param source 源对象，不能为 null
     * @param target 目标对象，不能为 null
     * @throws BeanUtilsException 拷贝过程出现错误时抛出
     */
    public static void updateProperties(@NonNull Object source, @NonNull Object target) {
        Assert.notNull(source, "源对象不能为 null");
        Assert.notNull(target, "目标对象不能为 null");

        // 将非空属性从源对象拷贝到目标对象
        try {
            org.springframework.beans.BeanUtils.copyProperties(source, target, getNullPropertyNames(source));
        } catch (BeansException e) {
            throw new BeanUtilsException("拷贝失败", e);
        }
    }

    /**
     * 将 Map 转化为指定类型的 Bean
     *
     * @param domainClass DOMAIN 类对象
     * @param valueMap    值的 Map
     * @param <DOMAIN>    DOMAIN 类型参数
     * @return BEAN
     */
    public static <DOMAIN> DOMAIN convertFromMap(@NonNull Class<DOMAIN> domainClass,
                                                 @NonNull Map<String, ?> valueMap) {
        Assert.notNull(domainClass, "原对象类不能为 null");
        Assert.notNull(valueMap, "值 Map 不能为 null");
        try {
            DOMAIN instance = domainClass.getConstructor().newInstance();
            org.apache.commons.beanutils.BeanUtils.populate(instance, valueMap);
            return instance;
        } catch (Exception e) {
            log.error("无法转化", e);
            throw new InternalException("不支持的转化：Map -> " + domainClass.getName());
        }
    }

    /**
     * 将一个 Bean 的属性使用指定 function 进行处理
     *
     * @param src         要处理的源对象
     * @param mapFunction 用于处理字符串属性的方法
     */
    public static void mapProperties(@NonNull Object src, @NonNull Function<String, String> mapFunction) {
        Assert.notNull(src, "源对象不能为 null");
        Assert.notNull(mapFunction, "映射函数不能为 null");

        BeanWrapperImpl beanWrapper = new BeanWrapperImpl(src);
        PropertyDescriptor[] propertyDescriptors = beanWrapper.getPropertyDescriptors();

        for (PropertyDescriptor propertyDescriptor : propertyDescriptors) {
            // 对字符串属性进行转化
            if (propertyDescriptor.getPropertyType().isAssignableFrom(String.class)) {
                String propertyValue;
                try {
                    propertyValue = (String) propertyDescriptor.getReadMethod().invoke(src);
                    propertyDescriptor.getWriteMethod().invoke(src, mapFunction.apply(propertyValue));
                } catch (IllegalAccessException | InvocationTargetException e) {
                    throw new BeanUtilsException("对属性值 [{}] 进行转化失败", e);
                }
            }
        }
    }

    /**
     * 获取空值属性名数组
     *
     * @param source 源对象，不能为 null
     */
    @NonNull
    private static String[] getNullPropertyNames(@NonNull Object source) {
        return getNullPropertyNameSet(source).toArray(new String[0]);
    }

    /**
     * 获取空值属性名集合
     *
     * @param source 源对象，不能为 null
     */
    @NonNull
    private static Set<String> getNullPropertyNameSet(@NonNull Object source) {

        Assert.notNull(source, "源对象不能为 null");
        BeanWrapperImpl beanWrapper = new BeanWrapperImpl(source);
        PropertyDescriptor[] propertyDescriptors = beanWrapper.getPropertyDescriptors();

        Set<String> emptyNames = new HashSet<>();

        for (PropertyDescriptor propertyDescriptor : propertyDescriptors) {
            String propertyName = propertyDescriptor.getName();
            Object propertyValue = beanWrapper.getPropertyValue(propertyName);

            // if property value is equal to null, add it to empty name set
            if (propertyValue == null) {
                emptyNames.add(propertyName);
            }
        }

        return emptyNames;
    }
}
