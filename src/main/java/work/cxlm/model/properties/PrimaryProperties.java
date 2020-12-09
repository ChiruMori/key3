package work.cxlm.model.properties;

/**
 * created 2020/11/14 23:07
 *
 * @author Chiru
 */
public enum PrimaryProperties implements PropertyEnum {

    /**
     * 图标
     */
    FAVICON_URL("favicon_url", String.class, "https://cxlm.work/upload/2020/09/favicon-4200eb5642b94655a0b1892b0dd6f6d6.png")
    ;

    private final String value;

    private final Class<?> type;

    private final String defaultValue;

    PrimaryProperties(String value, Class<?> type, String defaultValue) {
        this.value = value;
        this.type = type;
        this.defaultValue = defaultValue;
    }

    @Override
    public Class<?> getType() {
        return type;
    }

    @Override
    public String defaultValue() {
        return defaultValue;
    }

    @Override
    public String getValue() {
        return value;
    }
}
