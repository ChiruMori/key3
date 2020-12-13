package work.cxlm.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;
import work.cxlm.cache.AbstractStringCacheStore;
import work.cxlm.event.OptionUpdatedEvent;
import work.cxlm.exception.MissingPropertyException;
import work.cxlm.model.dto.OptionSimpleDTO;
import work.cxlm.model.dto.OptionDTO;
import work.cxlm.model.entity.Option;
import work.cxlm.model.enums.ValueEnum;
import work.cxlm.model.params.OptionParam;
import work.cxlm.model.params.OptionQuery;
import work.cxlm.model.properties.PropertyEnum;
import work.cxlm.repository.OptionRepository;
import work.cxlm.service.OptionService;
import work.cxlm.service.base.AbstractCrudService;
import work.cxlm.utils.ServiceUtils;
import work.cxlm.utils.ValidationUtils;

import javax.persistence.criteria.Predicate;
import java.util.*;

/**
 * created 2020/11/9 20:53
 *
 * @author ryanwang
 * @author johnniang
 * @author Chiru
 */
@Slf4j
@Service
public class OptionServiceImpl extends AbstractCrudService<Option, Integer> implements OptionService {

    private final OptionRepository optionRepository;
    private final AbstractStringCacheStore cacheStore;
    private final ApplicationEventPublisher eventPublisher;

    private final Map<String, PropertyEnum> propertyEnumMap;

    protected OptionServiceImpl(OptionRepository optionRepository,
                                AbstractStringCacheStore cacheStore,
                                ApplicationEventPublisher eventPublisher) {
        super(optionRepository);
        this.optionRepository = optionRepository;
        this.cacheStore = cacheStore;
        this.eventPublisher = eventPublisher;

        propertyEnumMap = Collections.unmodifiableMap(PropertyEnum.getValuePropertyEnumType());
    }

    @Override
    @Transactional
    public void save(Map<String, Object> options) {
        if (CollectionUtils.isEmpty(options)) {
            return;
        }

        Map<String, Option> oldOptionMap = ServiceUtils.convertToMap(listAll(), Option::getKey);
        List<Option> optionToCreate = new LinkedList<>();
        List<Option> optionToUpdate = new LinkedList<>();

        options.forEach((k, v) -> {
            // 注意，这里的 v 是字符串，不是 Option
            Option oldOption = oldOptionMap.get(k);
            if (oldOption == null || !StringUtils.equals(v.toString(), oldOption.getValue())) {
                // 生成 Param
                OptionParam param = new OptionParam();
                param.setKey(k);
                param.setValue(v.toString());
                ValidationUtils.validate(param);
                // 分别存储
                if (oldOption == null) {
                    optionToCreate.add(param.convertTo());
                } else {
                    param.update(oldOption);
                    optionToUpdate.add(oldOption);
                }
            }
        });

        updateInBatch(optionToUpdate);
        createInBatch(optionToCreate);

        if (!CollectionUtils.isEmpty(optionToCreate) || !CollectionUtils.isEmpty(optionToUpdate)) {
            publishOptionUpdateEvent();
        }
    }

    @Override
    @Transactional  // 因为调用了本类其他有事务的方法，因自调用问题，需要添加事务
    public void save(List<OptionParam> optionParams) {
        if (CollectionUtils.isEmpty(optionParams)) {
            return;
        }

        Map<String, Object> optionMap = ServiceUtils.convertToMap(optionParams, OptionParam::getKey, OptionParam::getValue);
        save(optionMap);
    }

    @Override
    public void save(OptionParam param) {
        if (param == null) {
            return;
        }
        Option opt = param.convertTo();
        create(opt);
        publishOptionUpdateEvent();
    }

    @Override
    public void update(@NonNull Integer optionId, OptionParam optionParam) {
        Assert.notNull(optionId, "配置项 ID 不能为 null");
        Option toUpdate = getById(optionId);
        optionParam.update(toUpdate);
        update(toUpdate);
        publishOptionUpdateEvent();
    }

    @Override
    public void saveProperty(@NonNull PropertyEnum property, String value) {
        Assert.notNull(property, "property 不能为 null");
        save(Collections.singletonMap(property.getValue(), value));  // 只有一个元素，无需事务
    }

    @Override
    @Transactional
    public void saveProperties(@NonNull Map<? extends PropertyEnum, String> properties) {
        if (CollectionUtils.isEmpty(properties)) {
            return;
        }
        Map<String, Object> optMap = new LinkedHashMap<>();
        properties.forEach((k, v) -> optMap.put(k.getValue(), v));
        save(optMap);
    }

    /**
     * 列出指定键的全部配置项
     *
     * @param keys 需要列出的 key 列表
     */
    @Override
    @NonNull
    public Map<String, Object> listOptions(List<String> keys) {
        if (CollectionUtils.isEmpty(keys)) {
            return Collections.emptyMap();
        }
        Map<String, Object> allOptions = listOptions();
        Map<String, Object> result = new HashMap<>();
        keys.stream()
                .filter(key -> !allOptions.containsKey(key))
                .forEach(key -> result.put(key, allOptions.get(key)));
        return result;
    }

    /**
     * 列出全部系统配置项，包括配置过的值和系统默认值
     */
    @Override
    @NonNull
    @SuppressWarnings("unchecked")
    public Map<String, Object> listOptions() {
        // 优先查找缓存，如果没有则执行查询
        return cacheStore.getAny(OPTION_KEY, Map.class).orElseGet(() -> {
            List<Option> allOptions = listAll();
            Set<String> ids = ServiceUtils.fetchIdOfDataCollections(allOptions, Option::getKey);

            // 用户自定义的配置项（含系统配置项）
            Map<String, Object> userOptionMap = ServiceUtils.convertToMap(allOptions, Option::getKey, opt -> {
                String optKey = opt.getKey();
                PropertyEnum propertyEnum = propertyEnumMap.get(optKey);
                if (propertyEnum == null) {
                    return opt.getValue();
                }

                return PropertyEnum.convertTo(opt.getValue(), propertyEnum);
            });
            HashMap<String, Object> result = new HashMap<>(userOptionMap);
            // 未修改过的系统默认配置项
            propertyEnumMap.keySet().stream()
                    .filter(key -> !ids.contains(key))  // 滤除已包含的
                    .forEach(key -> {
                        PropertyEnum propertyEnum = propertyEnumMap.get(key);
                        String defaultVal = propertyEnum.defaultValue();
                        if (StringUtils.isBlank(defaultVal)) {
                            return;
                        }
                        result.put(key, PropertyEnum.convertTo(defaultVal, propertyEnum));
                    });
            cacheStore.putAny(OPTION_KEY, result);  // 缓存整个 map
            return result;
        });
    }

    @Override
    public List<OptionDTO> listDTOs() {
        List<OptionDTO> result = new LinkedList<>();
        listOptions().forEach((key, val) -> result.add(new OptionDTO(key, val)));
        return result;
    }

    @Override
    public Page<OptionSimpleDTO> pageDTOsBy(@NonNull Pageable pageable, OptionQuery optionQuery) {
        Assert.notNull(pageable, "Pageable 对象不能为 null");
        Page<Option> optionPage = optionRepository.findAll(buildSpecByQuery(optionQuery), pageable);
        return optionPage.map(this::convertToDTO);
    }

    @Override
    @NonNull
    public OptionSimpleDTO convertToDTO(@NonNull Option option) {
        Assert.notNull(option, "Option 不能为 null");

        return new OptionSimpleDTO().convertFrom(option);
    }

    @NonNull
    private Specification<Option> buildSpecByQuery(@NonNull OptionQuery optionQuery) {
        Assert.notNull(optionQuery, "查询字段不能为 null");
        return (root, query, builder) -> {
            List<Predicate> predicates = new LinkedList<>();
            if (optionQuery.getType() != null) {
                // 关系语句：type=?
                predicates.add(builder.equal(root.get("type"), optionQuery.getType()));
            }
            if (optionQuery.getKey() != null) {
                // 关系语句 key LIKE %?% OR value LIKE %?%
                String likeCondition = String.format("%%%s%%", StringUtils.strip(optionQuery.getKey()));
                Predicate keyLike = builder.like(root.get("key"), likeCondition);
                Predicate valLike = builder.like(root.get("value"), likeCondition);
                predicates.add(builder.or(keyLike, valLike));
            }
            return query.where(predicates.toArray(new Predicate[0])).getRestriction();
        };
    }

    @Override
    @NonNull
    public Option removePermanently(@NonNull Integer id) {
        Option removedOption = removeById(id);
        publishOptionUpdateEvent();
        return removedOption;
    }

    @Override
    public Object getByKeyOfNullable(@NonNull String key) {
        return getByKey(key).orElse(null);
    }

    @Override
    @NonNull
    public Object getByKeyOfNonNull(@NonNull String key) {
        return getByKey(key).orElseThrow(() -> new MissingPropertyException("必须传递 key 参数"));
    }

    @Override
    public Object getByPropertyOfNullable(@NonNull PropertyEnum property) {
        return getByProperty(property).orElse(null);
    }

    @Override
    public Object getByPropertyOfNonNull(@NonNull PropertyEnum property) {
        Assert.notNull(property, "property 不能为 null");
        return getByKeyOfNonNull(property.getValue());
    }

    @Override
    public Optional<Object> getByProperty(@NonNull PropertyEnum property) {
        Assert.notNull(property, "property 不能为 null");
        return getByKey(property.getValue());
    }

    @Override
    public <T> Optional<T> getByProperty(@NonNull PropertyEnum property, @NonNull Class<T> type) {
        Assert.notNull(property, "property 不能为 null");
        return getByProperty(property).map(val -> PropertyEnum.convertTo(val.toString(), type));
    }

    @Override
    public <T> T getByPropertyOrDefault(@NonNull PropertyEnum property, @NonNull Class<T> type, T defaultValue) {
        return getByProperty(property, type).orElse(defaultValue);
    }

    @Override
    public <T> T getByPropertyOrDefault(@NonNull PropertyEnum property, @NonNull Class<T> type) {
        return getByProperty(property, type).orElse(property.defaultValue(type));
    }

    @Override
    public <T> T getByKeyOrDefault(@NonNull String key, @NonNull Class<T> valueType, T defaultValue) {
        return getByKey(key, valueType).orElse(defaultValue);
    }

    @Override
    public <T> Optional<T> getByKey(@NonNull String key, @NonNull Class<T> valueType) {
        return getByKey(key).map(v -> PropertyEnum.convertTo(v.toString(), valueType));
    }

    @Override
    @SuppressWarnings("unchecked")
    public Optional<Object> getByKey(@NonNull String key) {
        Assert.hasText(key, "键不能为 null");
        return Optional.ofNullable(listOptions().get(key));
    }

    @Override
    public <T extends Enum<T>> Optional<T> getEnumByProperty(@NonNull PropertyEnum property, @NonNull Class<T> valueType) {
        return getByProperty(property).map(v -> PropertyEnum.convertToEnum(v.toString(), valueType));
    }

    @Override
    public <T extends Enum<T>> T getEnumByPropertyOrDefault(@NonNull PropertyEnum property, @NonNull Class<T> valueType, T defaultValue) {
        return getEnumByProperty(property, valueType).orElse(defaultValue);
    }

    @Override
    public <T, E extends ValueEnum<T>> Optional<E> getValueEnumByProperty(PropertyEnum property, Class<T> valueType, Class<E> enumType) {
        return getByProperty(property).map(v -> ValueEnum.valueToEnum(enumType, PropertyEnum.convertTo(v.toString(), valueType)));
    }

    @Override
    public <T, E extends ValueEnum<T>> E getValueEnumByPropertyOrDefault(@NonNull PropertyEnum property, @NonNull Class<T> valueType,
                                                                         @NonNull Class<E> enumType, E defaultValue) {
        return getValueEnumByProperty(property, valueType, enumType).orElse(defaultValue);
    }

    private void publishOptionUpdateEvent() {
        flush();
        cleanCache();  // 别删这个，注意，与事件监听器中的 clearCache 并不是一个东西
        eventPublisher.publishEvent(new OptionUpdatedEvent(this));
    }

    private void cleanCache() {
        cacheStore.delete(OPTION_KEY);
    }
}
