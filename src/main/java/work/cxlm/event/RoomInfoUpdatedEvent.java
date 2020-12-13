package work.cxlm.event;

import org.springframework.context.ApplicationEvent;

/**
 * created 2020/12/10 23:43
 *
 * @author Chiru
 */
public class RoomInfoUpdatedEvent extends ApplicationEvent {
    /**
     * Create a new {@code ApplicationEvent}.
     *
     * @param source the object on which the event initially occurred or with
     *               which the event is associated (never {@code null})
     */
    public RoomInfoUpdatedEvent(Object source) {
        super(source);
    }
}
