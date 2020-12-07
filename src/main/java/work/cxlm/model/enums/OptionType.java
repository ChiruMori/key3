package work.cxlm.model.enums;

/**
 * 全局功能选项类型
 * created 2020/11/9 15:17
 *
 * @author Chiru
 * @author ryanwang
 * @deprecated 本系统中不涉及复杂的 Option 状态
 */
@Deprecated
public enum OptionType implements ValueEnum<Integer> {
    INTERNAL(0),  // 内部选项
    CUSTOM(1)  // 用户选项
    ;

    private Integer value;

    OptionType(Integer val) {
        value = val;
    }

    @Override
    public Integer getValue() {
        return value;
    }
}
