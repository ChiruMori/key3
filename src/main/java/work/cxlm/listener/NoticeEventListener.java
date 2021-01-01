package work.cxlm.listener;

import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import work.cxlm.event.NoticeEvent;
import work.cxlm.service.NoticeService;

/**
 * created 2020/12/10 15:58
 *
 * @author Chiru
 * @deprecated 不再使用事件机制处理 Notice，请直接使用 Service
 */
// @Component
@Deprecated
public class NoticeEventListener {

    private final NoticeService noticeService;

    NoticeEventListener(NoticeService noticeService) {
        this.noticeService = noticeService;
    }

    @Async
    @EventListener
    public void onNoticeEvent(NoticeEvent noticeEvent) {
        noticeService.createBy(noticeEvent.getParam());
    }
}
