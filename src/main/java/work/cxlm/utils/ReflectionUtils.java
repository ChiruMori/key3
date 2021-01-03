package work.cxlm.utils;

import org.springframework.beans.BeanWrapperImpl;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;

import java.beans.PropertyDescriptor;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Map;

/**
 * @author cxlm
 * created 2020/10/17 15:34
 * <p>
 * 反射工具类、静态类
 */
public class ReflectionUtils {

    /**
     * 通过反射从对象获取属性值，要求该类必须有该属性的 getter 方法
     *
     * @param fieldName 属性名
     * @param obj       源对象
     * @return 读取的属性值，如果无法读取将返回 null，同时打印错误栈
     */
    public static Object getFieldValue(@NonNull String fieldName, @NonNull Object obj) {
        Assert.notNull(fieldName, "字段名不能为 null");
        Assert.notNull(obj, "源对象不能为 null");

        Object value = null;
        try {
            // 拼装 getter 方法
            String firstLetter = fieldName.substring(0, 1).toUpperCase();
            String getterName = "get" + firstLetter + fieldName.substring(1);
            Method method = obj.getClass().getMethod(getterName);
            value = method.invoke(obj);
        } catch (Exception e) {
            e.printStackTrace(); // 无法通过 getter 获得属性值
        }
        return value;
    }

    /**
     * 获取接口的泛型，递归向上读取
     *
     * @param interfaceType       接口类型，不能为 null
     * @param implementationClass 实现类，不能为 null
     * @return 接口的泛型，不存在时返回 null
     */
    @Nullable
    public static ParameterizedType getParameterizedType(@NonNull Class<?> interfaceType, Class<?> implementationClass) {
        Assert.notNull(interfaceType, "interfaceType 不能为 null");
        Assert.isTrue(interfaceType.isInterface(), "interfaceType 必须是接口");

        if (implementationClass == null) {
            // If the super class is Object parent then return null
            return null;
        }

        // 在接口上搜索泛型
        ParameterizedType currentType = getParameterizedType(interfaceType, implementationClass.getGenericInterfaces());

        if (currentType != null) {
            return currentType;
        }

        Class<?> superclass = implementationClass.getSuperclass();

        // 在类上搜索泛型（递归向上）
        return getParameterizedType(interfaceType, superclass);
    }


    /**
     * 获取泛型
     *
     * @param superType    父类或父接口，不能为 null
     * @param genericTypes 类型数组
     * @return 父类或接口的泛型，不匹配时返回 null
     */
    @Nullable
    public static ParameterizedType getParameterizedType(@NonNull Class<?> superType, Type... genericTypes) {
        Assert.notNull(superType, "接口或父类型不能为 null");

        ParameterizedType currentType = null;

        for (Type genericType : genericTypes) {
            if (genericType instanceof ParameterizedType) {
                ParameterizedType parameterizedType = (ParameterizedType) genericType;
                if (parameterizedType.getRawType().getTypeName().equals(superType.getTypeName())) {
                    currentType = parameterizedType;
                    break;
                }
            }
        }

        return currentType;
    }
}
