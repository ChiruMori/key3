package work.cxlm.event;

import org.springframework.context.ApplicationEvent;

/**
 * created 2021/1/4 14:09
 *
 * @author Chiru
 */
public class JoiningOrBelongUpdatedEvent extends ApplicationEvent {

    /**
     * Create a new {@code ApplicationEvent}.
     *
     * @param source the object on which the event initially occurred or with
     *               which the event is associated (never {@code null})
     */
    public JoiningOrBelongUpdatedEvent(Object source) {
        super(source);
    }
}
