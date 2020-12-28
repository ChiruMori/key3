package work.cxlm.model.enums;

/**
 * 通知类型
 * created 2020/12/10 14:28
 *
 * @author Chiru
 */
public enum NoticeType implements ValueEnum<Integer> {

    // 由社团管理员发布的普通公告
    CLUB_ANNOUNCEMENT(1, "新公告"),

    // 由系统管理员发布的公告，将会通知全部用户
    ADMIN_ANNOUNCEMENT(2, "管理公告"),

    // 废弃: 关注时段可用通知
    FOLLOW_AVAILABLE(3, "关注时段可用"),

    // 给其他用户留言时的通知
    NEW_MESSAGE(4, "用户留言"),

    // 预约的时间开始前的提醒
    TIME_START(5, "预约通知"),

    // 用户的权限被修改
    AUTHORITY_CHANGED(6, "权限变更"),

    // 活动室新一周的预定可以开始的通知
    TIME_RESET(7, "周次重置"),

    // 预约的时段被删除的通知
    TIME_DELETE(8, "活动室时段删除");

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

    /**
     * 系统通知类型
     */
    public boolean anncounceType() {
        return this == CLUB_ANNOUNCEMENT || this == ADMIN_ANNOUNCEMENT;
    }
}
