package work.cxlm.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import org.springframework.transaction.annotation.Transactional;
import work.cxlm.model.dto.OptionSimpleDTO;
import work.cxlm.model.dto.OptionDTO;
import work.cxlm.model.entity.Option;
import work.cxlm.model.enums.ValueEnum;
import work.cxlm.model.params.OptionParam;
import work.cxlm.model.params.OptionQuery;
import work.cxlm.model.properties.PropertyEnum;
import work.cxlm.service.base.CrudService;

import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * created 2020/11/9 15:11
 *
 * @author johnniang
 * @author ryanwang
 * @author Chiru
 */
public interface OptionService extends CrudService<Option, Integer> {

    /**
     * 批量保存配置项
     *
     * @param options 配置项的键值集合
     */
    @Transactional(rollbackFor = Exception.class)
    void save(@Nullable Map<String, Object> options);

    /**
     * 批量保存配置项
     *
     * @param optionParams 配置项参数列表
     */
    @Transactional(rollbackFor = Exception.class)
    void save(@Nullable List<OptionParam> optionParams);

    /**
     * 保存单个配置项
     *
     * @param param 配置项参数
     */
    @Transactional(rollbackFor = Exception.class)
    void save(@Nullable OptionParam param);

    /**
     * 更新单个配置项
     *
     * @param optionId 配置项 ID
     * @param optionParam 配置项参数
     */
    void update(@NonNull Integer optionId, @NonNull OptionParam optionParam);

    /**
     * 保存一个属性值
     *
     * @param property 配置项的枚举类型键
     * @param value 配置项的值
     */
    @Transactional(rollbackFor = Exception.class)
    void saveProperty(@NonNull PropertyEnum property, @Nullable String value);

    /**
     * 保存一组属性
     *
     * @param properties 配置项枚举类型键、值的集合
     */
    void saveProperties(@NonNull Map<? extends PropertyEnum, String> properties);

    /**
     * 根据给出的 key 列表列出一组属性
     *
     * @param keys 要查询的键列表
     * @return 配置项的键值列表
     */
    @NonNull
    Map<String, Object> listOptions(@Nullable List<String> keys);

    /**
     * 列出全部配置项
     *
     * @return 全部配置项的键值列表
     */
    @NonNull
    Map<String, Object> listOptions();

    /**
     * 列出全部配置项的 DTO
     *
     * @return 全部配置项的 DTO 列表
     */
    List<OptionDTO> listDtos();

    /**
     * 查询指定分页的配置项 DTO
     *
     * @param pageable 分页参数
     * @param optionQuery 查询参数
     * @return 配置项的 DTO 分页数据集
     */
    Page<OptionSimpleDTO> pageDtosBy(@NonNull Pageable pageable, OptionQuery optionQuery);

    /**
     * 永久移除配置项
     *
     * @param id 配置项 ID
     * @return 被移除的配置项值
     */
    @NonNull
    Option removePermanently(@NonNull Integer id);

    /**
     * 通过键寻找配置项，可能得到 null
     *
     * @param key 要查询的配置项键
     * @return 查找到的配置项值
     */
    @Nullable
    Object getByKeyOfNullable(@NonNull String key);

    /**
     * 通过键寻找配置项，不会得到 null
     *
     * @param key 要查找的配置项键
     * @return 查找到的配置项值
     */
    @NonNull
    Object getByKeyOfNonNull(@NonNull String key);

    /**
     * 通过属性查找配置项，可能得到 null
     *
     * @param property 配置项的枚举类型键
     * @return 目标配置项值
     */
    Object getByPropertyOfNullable(@NonNull PropertyEnum property);

    /**
     * 通过属性查找配置项，不会得到 null
     *
     * @param property 配置项的枚举类型键
     * @return 目标配置项值
     */
    Object getByPropertyOfNonNull(@NonNull PropertyEnum property);

    /**
     * 通过属性查找配置项，得到 Optional 包装的结果
     *
     * @param property 配置项的枚举类型键
     * @return 目标配置项值
     */
    Optional<Object> getByProperty(@NonNull PropertyEnum property);

    /**
     * 通过属性查找配置项，同时指定返回值类型，得到 Optional 包装的结果
     *
     * @param property 配置项的枚举类型键
     * @param type 返回值类对象
     * @param <T> 返回值类型参数
     * @return 目标配置项值
     */
    <T> Optional<T> getByProperty(@NonNull PropertyEnum property, @NonNull Class<T> type);

    /**
     * 通过属性查找配置项，并指定返回值类型和默认值
     *
     * @param property 配置项的枚举类型键
     * @param type 返回值类对象
     * @param <T> 返回值类型参数
     * @param defaultValue 如果配置项不存在返回的默认值
     * @return 目标配置项值
     */
    <T> T getByPropertyOrDefault(@NonNull PropertyEnum property, @NonNull Class<T> type, T defaultValue);

    /**
     * 通过属性查找配置项，并指定返回值类型，使用内置的默认值
     *
     * @param property 配置项的枚举类型键
     * @param type 返回值类对象
     * @param <T> 返回值类型参数
     * @return 目标配置项值
     */
    <T> T getByPropertyOrDefault(@NonNull PropertyEnum property, @NonNull Class<T> type);

    /**
     * 通过键查找配置项的值，同时指定默认值
     *
     * @param key 配置项的键
     * @param valueType 返回值类对象
     * @param <T> 返回值类型参数
     * @param defaultValue 如果配置项不存在返回的默认值
     * @return 目标配置项值
     */
    <T> T getByKeyOrDefault(@NonNull String key, @NonNull Class<T> valueType, T defaultValue);

    /**
     * 通过键查找配置项的值，同时指定返回值类型，得到 Optional 包装的结果
     *
     * @param key 配置项的键
     * @param valueType 返回值类对象
     * @param <T> 返回值类型参数
     * @return 目标配置项值
     */
    <T> Optional<T> getByKey(@NonNull String key, @NonNull Class<T> valueType);

    /**
     * 通过键查找配置项的值，得到 Optional 包装的结果
     *
     * @param key 配置项的键
     * @param <T> 返回值类型参数
     * @return 目标配置项值
     */
    <T> Optional<T> getByKey(@NonNull String key);

    /**
     * 通过键查找枚举类型配置项的值，同时指定返回值类型，得到 Optional 包装的结果
     *
     * @param property 配置项的枚举类型键
     * @param valueType 返回值类对象
     * @param <T> 返回值类型参数
     * @return 目标配置项值
     */
    <T extends Enum<T>> Optional<T> getEnumByProperty(@NonNull PropertyEnum property, @NonNull Class<T> valueType);

    /**
     * 通过键查找枚举类型配置项的值，同时指定返回值类型、默认值，得到 Optional 包装的结果
     *
     * @param property 配置项的枚举类型键
     * @param valueType 返回值类对象
     * @param <T> 返回值类型参数
     * @param defaultValue 不存在时返回的默认值
     * @return 目标配置项值
     */
    <T extends Enum<T>> T getEnumByPropertyOrDefault(@NonNull PropertyEnum property, @NonNull Class<T> valueType, T defaultValue);

    /**
     * 通过键查找枚举类型配置项的值（ValueEnum 类实例），同时指定返回值类型、枚举类类型，得到 Optional 包装的结果
     *
     * @param property 配置项的枚举类型键
     * @param valueType 返回值类对象
     * @param enumType 枚举类型键的实际值类对象
     * @param <T> 返回值类型参数
     * @param <E> 枚举类型键的实际值类型参数
     * @return 目标配置项值
     */
    <T, E extends ValueEnum<T>> Optional<E> getValueEnumByProperty(PropertyEnum property, Class<T> valueType, Class<E> enumType);

    /**
     * 通过键查找枚举类型配置项的值（ValueEnum 类实例），同时指定返回值类型、枚举类类型、默认值，得到 Optional 包装的结果
     *
     * @param property 配置项的枚举类型键
     * @param valueType 返回值类对象
     * @param enumType 枚举类型键的实际值类对象
     * @param <T> 返回值类型参数
     * @param <E> 枚举类型键的实际值类型参数
     * @param defaultValue 不存在时返回的默认值
     * @return 目标配置项值
     */
    <T, E extends ValueEnum<T>> E getValueEnumByPropertyOrDefault(@NonNull PropertyEnum property, @NonNull Class<T> valueType, @NonNull Class<E> enumType, E defaultValue);

    /**
     * 将 Option 实例转化为 OptionSimpleDTO 对象
     *
     * @param option 源 Option 实例
     * @return OptionSimpleDTO
     */
    @NonNull
    OptionSimpleDTO convertToDTO(@NonNull Option option);

}
