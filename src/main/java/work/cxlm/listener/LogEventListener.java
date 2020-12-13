package work.cxlm.listener;

import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import work.cxlm.event.LogEvent;
import work.cxlm.model.entity.Log;
import work.cxlm.service.LogService;

/**
 * created 2020/10/29 15:15
 *
 * @author johnniang
 * @author cxlm
 */
@Component
public class LogEventListener {

    private final LogService logService;

    public LogEventListener(LogService logService) {
        this.logService = logService;
    }

    // 监听 LogEvent，异步方法（在独立的线程中执行）
    @Async
    @EventListener
    public void onApplicationEvent(LogEvent logEvent) {
        Log logToCreate = logEvent.getParam().convertTo();
        logService.create(logToCreate);
    }
}
