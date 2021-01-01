package work.cxlm.model.dto.base;

import org.springframework.lang.NonNull;
import work.cxlm.utils.BeanUtils;

/**
 * 将实体类转化为 DTO 的接口
 * created 2020/10/29 15:19
 *
 * @param <DTO>    本接口的实现类
 * @param <DOMAIN> 实体类
 * @author johnniang
 * @author cxlm
 */
public interface OutputConverter<DTO extends OutputConverter<DTO, DOMAIN>, DOMAIN> {

    /**
     * 将实体类对象转化为 DTO 对象
     *
     * @param domain 实体类对象
     * @param <T>    DTO 类参数
     * @return 转化后的 DTO 类对象
     */
    @NonNull
    @SuppressWarnings("unchecked")
    default <T extends DTO> T convertFrom(DOMAIN domain) {
        BeanUtils.updateProperties(domain, this);
        return (T) this;
    }
}
