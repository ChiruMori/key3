package work.cxlm.utils;

import cn.hutool.core.collection.CollectionUtil;
import org.springframework.data.domain.*;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;
import work.cxlm.model.entity.User;
import work.cxlm.model.vo.PageUserVO;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 供 Service 使用的静态工具类
 * created 2020/11/9 22:12
 *
 * @author johnniang
 * @author Chiru
 */
public class ServiceUtils {

    private ServiceUtils() {
    }

    /**
     * 提取一组数据的 ID 到集合中
     *
     * @param entities        原始数据
     * @param convertFunction 从原始数据中提取 ID 的函数
     * @param <ID>            ID 类型参数
     * @param <T>             原始数据类型参数
     */
    @NonNull
    public static <ID, T> Set<ID> fetchIdOfDataCollections(Collection<T> entities, @NonNull Function<T, ID> convertFunction) {
        Assert.notNull(convertFunction, "ID 转换函数不能为 null");
        return CollectionUtils.isEmpty(entities) ?
                Collections.emptySet() :
                entities.stream().map(convertFunction).collect(Collectors.toSet());
    }

    /**
     * 将列表中的数据转化为 ID 映射的 Map
     *
     * @param ids             id 集合，确保在结果 Map 中，集合内的 ID 一定对应着 List，注意：当 entities 中没有对应的 ID 时，对应的 List 是一个空列表
     * @param entities        被整理的实例列表
     * @param convertFunction 将实例转化为 ID 的函数
     * @param <ID>            ID 类型参数
     * @param <ENTITY>        实例的类型函数
     * @return （每个 ID 都会对应一个只有一个元素的列表）的 Map
     */
    @NonNull
    public static <ID, ENTITY> Map<ID, List<ENTITY>> convertToListMap(Collection<ID> ids, List<ENTITY> entities,
                                                                      @NonNull Function<ENTITY, ID> convertFunction) {
        Assert.notNull(convertFunction, "ID 转换函数不能为 null");
        if (CollectionUtils.isEmpty(ids) || CollectionUtils.isEmpty(entities)) {
            return Collections.emptyMap();
        }

        Map<ID, List<ENTITY>> res = new HashMap<>();
        // 整理数据，每个 ID 都会对应一个只有一个元素的列表
        entities.forEach(e -> res.computeIfAbsent(convertFunction.apply(e), id -> new LinkedList<>())
                .add(e));
        // 为确保所有的 id 都对应有元素，将不存在 entity 的 id 的值设置为空列表
        ids.forEach(id -> res.putIfAbsent(id, Collections.emptyList()));

        return res;
    }

    /**
     * 将列表中的数据整理为 Map
     *
     * @param entities        数据实体列表
     * @param convertFunction 将数据实体转化为 ID 的函数
     * @param <ID>            ID 类型参数
     * @param <ENTITY>        实体类型参数
     */
    public static <ID, ENTITY> Map<ID, ENTITY> convertToMap(List<ENTITY> entities,
                                                            @NonNull Function<ENTITY, ID> convertFunction) {
        Assert.notNull(convertFunction, "转换函数不能为 null");

        if (CollectionUtil.isEmpty(entities)) {
            return Collections.emptyMap();
        }

        HashMap<ID, ENTITY> resultMap = new HashMap<>();
        entities.forEach(data -> resultMap.putIfAbsent(convertFunction.apply(data), data));
        return resultMap;
    }

    /**
     * 将列表中的数据整理为 Map
     *
     * @param entities     数据实体列表
     * @param idConverter  从 entity 中提取 ID 的转换函数
     * @param valConverter 从 entity 中提取 val 的转换函数
     * @param <ID>         ID 类型参数
     * @param <ENTITY>     实体类型参数
     * @param <VAL>        value 类型参数
     */
    public static <ID, ENTITY, VAL> Map<ID, VAL> convertToMap(List<ENTITY> entities, @NonNull Function<ENTITY, ID> idConverter,
                                                              @NonNull Function<ENTITY, VAL> valConverter) {
        Assert.notNull(idConverter, "ID 转换函数不能为 null");
        Assert.notNull(valConverter, "value 转换函数不能为 null");

        if (CollectionUtils.isEmpty(entities)) {
            return Collections.emptyMap();
        }

        HashMap<ID, VAL> resultMap = new HashMap<>();
        entities.forEach(data -> resultMap.putIfAbsent(idConverter.apply(data), valConverter.apply(data)));
        return resultMap;
    }

    /**
     * 检验给定的 ID 是否为空 ID，为 null 或者值 <= 0 时，判定该 ID 为 null
     */
    public static boolean isEmptyId(@Nullable Number id) {
        return id == null || id.longValue() <= 0;
    }

    /**
     * 建立排序请求对象（Pageable 对象）
     *
     * @param top          显示前多少项
     * @param sortProperty 排序依据参数
     */
    public static Pageable buildLatestPageable(int top, @NonNull String sortProperty) {
        Assert.isTrue(top > 0, "top 参数必须为非零正整数");
        Assert.notNull(sortProperty, "排序依据参数不能为 null");

        return PageRequest.of(0, top, Sort.by(Sort.Direction.DESC, sortProperty));
    }

    /**
     * 建立排序请求对象（Pageable 对象），使用的排序依据为建立时间
     *
     * @param top 显示前多少项
     */
    public static Pageable buildLatestPageable(int top) {
        return buildLatestPageable(top, "createTime");
    }

    /**
     * 构建一个没有任何元素的分页
     */
    public static <VO, ENTITY> Page<VO> buildEmptyPageImpl(@NonNull Page<ENTITY> page) {
        Assert.notNull(page, "Page 参数不能为 null");

        return new PageImpl<>(Collections.emptyList(), page.getPageable(), 0);
    }

    /**
     * 使用指定的函数转化分页内元素类型
     *
     * @param srcPage   原分页
     * @param pageable  分页实例，可以为 null
     * @param converter 转化函数
     * @param <SRC>     源实例类型
     * @param <TAR>     目标实例类型
     */
    public static <SRC, TAR> Page<TAR> convertPageElements(@NonNull Page<SRC> srcPage, @Nullable Pageable pageable,
                                                           @NonNull Function<SRC, TAR> converter) {
        Assert.notNull(srcPage, "原始分页不能为 Null");
        Assert.notNull(converter, "转换函数不能为 null");

        if (pageable == null) {
            pageable = buildLatestPageable(srcPage.getSize());
        }

        LinkedList<TAR> targetList = new LinkedList<>();
        srcPage.stream().forEach(src -> targetList.add(converter.apply(src)));
        return new PageImpl<>(targetList, pageable, srcPage.getTotalElements());
    }

    /**
     * 使用转换函数转换列表中元素的类型
     * @param srcList 原始列表
     * @param converter 转换函数
     * @param <SRC> 原始元素类型
     * @param <TAR> 目标元素类型
     */
    @NonNull
    public static <SRC, TAR> List<TAR> convertList(@Nullable List<SRC> srcList, @NonNull Function<SRC, TAR> converter) {
        Assert.notNull(converter, "转换函数");
        if (CollectionUtils.isEmpty(srcList)) {
            return Collections.emptyList();
        }
        return srcList.stream().map(converter).collect(Collectors.toList());
    }

    /**
     * 整理数据，将实例列表整理为 key 对应 Value 列表的映射关系
     * @param srcList 源实例列表
     * @param keyConverter 将实例转化为 key 的函数
     * @param valueConverter 将实例转化为 value 的函数
     * @param <K> 键类型
     * @param <V> 值类型
     * @param <SRC> 源数据类型
     */
    @NonNull
    public static <K, V, SRC> Map<K, List<V>> list2ListMap(@NonNull List<SRC> srcList,
                                                           @NonNull Function<SRC, K> keyConverter,
                                                           @NonNull Function<SRC, V> valueConverter) {
        Assert.notNull(srcList, "源 List 不能为 null");
        Assert.notNull(keyConverter, "key 转换函数不能为 null");
        Assert.notNull(valueConverter, "value 转换函数不能为 null");

        Map<K, List<V>> res = new HashMap<>();
        srcList.forEach(src -> {
            K key = keyConverter.apply(src);
            V value = valueConverter.apply(src);
            if (res.containsKey(key)){
                res.get(key).add(value);
            }else {
                LinkedList<V> valList = new LinkedList<>();
                valList.add(value);
                res.put(key, valList);
            }
        });
        return res;
    }

}