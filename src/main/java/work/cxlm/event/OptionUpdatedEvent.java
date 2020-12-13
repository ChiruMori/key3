package work.cxlm.event;

import org.springframework.context.ApplicationEvent;

/**
 * 系统配置项更新事件
 * created 2020/11/12 14:21
 *
 * @author johnniang
 * @author Chiru
 */
public class OptionUpdatedEvent extends ApplicationEvent {

    public OptionUpdatedEvent(Object source) {
        super(source);
    }
}
