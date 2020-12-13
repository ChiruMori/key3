package work.cxlm.model.enums;

import org.springframework.lang.NonNull;

/**
 * 用户接受的消息类型
 * created 2020/12/11 14:07
 * 均采用微信消息
 *
 * @author Chiru
 */
@Deprecated
public enum MessageType implements ValueEnum<Integer>{

    /**
     * 不接受消息
     */
    NONE(0),

    /**
     * 接受微信消息提醒
     */
    WECHAT_MSG(10),

    /**
     * 接受邮件消息
     */
    EMAIL_MSG(5),
    ;

    private final Integer value;

    MessageType(Integer value) {
        this.value = value;
    }

    @Override
    @NonNull
    public Integer getValue() {
        return value;
    }

    /**
     * 判断当前消息类型优先级是否高于另一类型
     */
    public boolean priorityTo(MessageType other) {
        return value > other.getValue();
    }
}
