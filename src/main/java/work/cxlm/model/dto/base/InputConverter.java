package work.cxlm.model.dto.base;

import org.springframework.lang.Nullable;
import work.cxlm.utils.BeanUtils;
import work.cxlm.utils.ReflectionUtils;

import java.lang.reflect.ParameterizedType;
import java.util.Objects;

/**
 * 将输入(Param)转为实体(DOMAIN)的转换接口
 *
 * @author johnniang
 * @author cxlm
 */
public interface InputConverter<DOMAIN> {

    /**
     * 转化为 DOMAIN 实例（浅拷贝）
     *
     * @return 转化后的实体
     */
    @SuppressWarnings("unchecked")
    default DOMAIN convertTo() {

        // Get parameterized type
        ParameterizedType currentType = parameterizedType();

        // 断言 currentType 不为 null
        Objects.requireNonNull(currentType, "参数化类型为 null，无法获取实际类型");

        Class<DOMAIN> domainClass = (Class<DOMAIN>) currentType.getActualTypeArguments()[0];

        return BeanUtils.transformFrom(this, domainClass);
    }

    /**
     * 通过 DTO 更新 DOMAIN 实例
     *
     * @param domain 被更新的实体对象
     */
    default void update(DOMAIN domain) {
        BeanUtils.updateProperties(this, domain);
    }

    /**
     * 获取泛型
     *
     * @return 获取泛型
     */
    @Nullable
    default ParameterizedType parameterizedType() {
        return ReflectionUtils.getParameterizedType(InputConverter.class, this.getClass());
    }
}
