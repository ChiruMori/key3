package work.cxlm.listener;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import work.cxlm.event.JoiningOrBelongUpdatedEvent;
import work.cxlm.event.LogEvent;
import work.cxlm.model.entity.Log;
import work.cxlm.task.RoomRefreshTask;

/**
 * created 2020/12/10 23:48
 *
 * @author Chiru
 */
@Component
@Slf4j
public class JoiningOrBelongUpdatedEventListener {

    private final RoomRefreshTask roomRefreshTask;

    JoiningOrBelongUpdatedEventListener(RoomRefreshTask task) {
        this.roomRefreshTask = task;
    }

    /**
     * 监听 LogEvent，异步方法（在独立的线程中执行）
     */
    @Async
    @EventListener
    public void onApplicationEvent(JoiningOrBelongUpdatedEvent updatedEvent) {
        log.debug("通知 RoomRefreshTask 更新活动室、成员关系缓存");
        roomRefreshTask.clearCache();
    }
}
