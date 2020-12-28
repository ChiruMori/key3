package work.cxlm.model.properties;

/**
 * 邮件相关配置
 * created 2020/11/13 17:14
 *
 * @author johnniang
 * @author Chiru
 */
public enum EmailProperties implements PropertyEnum {

    // 服务器
    HOST("email_host", String.class, "smtp.ym.163.com"),
    // 协议
    PROTOCOL("email_protocol", String.class, "smtp"),
    // 端口
    SSL_PORT("email_ssl_port", Integer.class, "465"),
    // 邮件用户名
    USERNAME("email_username", String.class, "cxlm@cxlm.work"),
    // 邮件密码
    PASSWORD("email_password", String.class, "cxlmmima123456"),
    // 发件人
    FROM_NAME("email_from_name", String.class, "琴房助手3");

    private final String value;

    private final Class<?> type;

    private final String defaultValue;

    EmailProperties(String value, Class<?> type, String defaultValue) {
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
    public String defaultValue() {
        return defaultValue;
    }

    @Override
    public String getValue() {
        return value;
    }
}
