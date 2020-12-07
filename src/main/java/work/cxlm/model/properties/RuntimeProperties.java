package work.cxlm.model.properties;

import org.springframework.lang.NonNull;

/**
 * 包含系统运行需要的其他参数
 * created 2020/12/6 21:53
 *
 * @author Chiru
 */
public enum RuntimeProperties implements PropertyEnum {

    WEEK_START_DATE("week_start_stamp", Long.class, "0")
    ;
    private final String value;

    private final Class<?> type;

    private final String defaultValue;

    RuntimeProperties(String value, Class<?> type, String defaultValue) {
        this.defaultValue = defaultValue;
        if (!PropertyEnum.isSupportedType(type)) {
            throw new IllegalArgumentException("不支持的参数类型: " + type);
        }

        this.value = value;
        this.type = type;
    }

    @Override
    public Class<?> getType() {
        return type;
    }

    @Override
    @NonNull
    public String defaultValue() {
        return defaultValue;
    }

    @Override
    public String getValue() {
        return value;
    }
}
