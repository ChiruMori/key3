package work.cxlm.event;

import org.springframework.context.ApplicationEvent;
import work.cxlm.model.params.NoticeParam;
import work.cxlm.utils.ValidationUtils;

/**
 * created 2020/12/10 15:45
 *
 * @author Chiru
 * @deprecated 不再使用事件机制处理 Notice，请直接使用 Service
 */
@Deprecated
public class NoticeEvent extends ApplicationEvent {

    private final NoticeParam param;

    /**
     * Create a new {@code ApplicationEvent}.
     *
     * @param source the object on which the event initially occurred or with
     *               which the event is associated (never {@code null})
     */
    public NoticeEvent(Object source, NoticeParam param) {
        super(source);

        ValidationUtils.validate(param);
        this.param = param;
    }

    public NoticeParam getParam() {
        return param;
    }
}
