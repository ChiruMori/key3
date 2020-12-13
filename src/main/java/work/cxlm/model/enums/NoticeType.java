package work.cxlm.model.enums;

/**
 * 通知类型
 * created 2020/12/10 14:28
 *
 * @author Chiru
 */
public enum NoticeType implements ValueEnum<Integer> {

    CLUB_ANNOUNCEMENT(1, "新公告"), // TODO

    ADMIN_ANNOUNCEMENT(2, "管理公告"), // TODO

    FOLLOW_AVAILABLE(3, "关注时段可用"), // TODO: 改为邮件通知

    NEW_MESSAGE(4, "用户留言"), // TODO

    TIME_START(5, "预约通知"),

    AUTHORITY_CHANGED(6, "权限变更"),

    TIME_RESET(7, "周次重置"),
    ;

    private final Integer value;
    private final String title;

    NoticeType(Integer value, String title) {
        this.value = value;
        this.title = title;
    }

    @Override
    public Integer getValue() {
        return value;
    }

    public String getTitle() {
        return title;
    }
}
