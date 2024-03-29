package work.cxlm.model.enums;

/**
 * created 2020/10/22 17:25
 *
 * @author cxlm
 */
public enum LogType implements ValueEnum<Integer> {

    /**
     * 管理后台用户登录
     */
    LOGGED_IN(10),

    /**
     * 管理后台登录失败
     */
    LOGGED_FAILED(11),

    /**
     * 用户登录小程序的日志，因为生成太多的日志信息已弃用
     */
    @Deprecated
    MINI_LOGGED_IN(15),

    /**
     * 指定了新的用户
     */
    NEW_USER(16),

    /**
     * 指定了新的社团管理员
     */
    NEW_CLUB_ADMIN(20),

    /**
     * 指定了新的系统管理员
     */
    NEW_SYSTEM_ADMIN(21),

    AUTH_REVOKE(22),

    NEW_CLUB(30),

    DELETE_CLUB(31),

    /**
     * 救急工具包相关日志
     */
    EMERGENCY_KIT(40),


    /**
     * 社团级日志
     */
    CLUB_EVENT(100),

    ;
    private final Integer value;

    LogType(Integer value) {
        this.value = value;
    }

    @Override
    public Integer getValue() {
        return value;
    }

    /**
     * 判断当前日志是否为系统级别日志
     */
    public boolean systemLevel() {
        return this != CLUB_EVENT;
    }
}
