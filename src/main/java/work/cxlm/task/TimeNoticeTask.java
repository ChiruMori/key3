package work.cxlm.task;

import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import work.cxlm.model.entity.Notice;
import work.cxlm.model.entity.Room;
import work.cxlm.model.entity.TimePeriod;
import work.cxlm.model.entity.support.TimeIdGenerator;
import work.cxlm.model.enums.NoticeType;
import work.cxlm.service.NoticeService;
import work.cxlm.service.RoomService;
import work.cxlm.service.TimeService;
import work.cxlm.utils.DateUtils;
import work.cxlm.utils.ServiceUtils;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * created 2020/12/17 14:34
 *
 * @author Chiru
 */
@Component
@EnableAsync
@EnableScheduling
@Slf4j
public class TimeNoticeTask {

    private final TimeService timeService;
    private final RoomService roomService;
    private final NoticeService noticeService;

    public TimeNoticeTask(TimeService timeService,
                          RoomService roomService,
                          NoticeService noticeService) {
        this.timeService = timeService;
        this.roomService = roomService;
        this.noticeService = noticeService;
    }

    /**
     * 每到半点执行一次，用于发送预约提醒
     * 秒 分 时 日 月 星期
     */
    @Async
    @Scheduled(cron = "0 30 * * * ?")
    public void beforeTimeStart() {
        DateUtils nextTopHourPointer = new DateUtils().nextTopHour();

        List<Room> rooms = roomService.listAll();
        Map<Integer, Room> roomMap = ServiceUtils.convertToMap(rooms, Room::getId);
        // 获取各活动室下一时段
        List<Long> timeIds = new LinkedList<>();
        rooms.forEach(room -> timeIds.add(TimeIdGenerator.encodeId(nextTopHourPointer, room.getId())));
        List<TimePeriod> validTimePeriods = timeService.listAllByIds(timeIds);
        // 构建并发送通知
        List<Notice> notices = new LinkedList<>();
        validTimePeriods.forEach(time -> notices.add(new Notice(NoticeType.TIME_START,
                "您在活动室【" + roomMap.get(time.getRoomId()).getName() + "】预约的时段: [" +
                        nextTopHourPointer.getFormattedTime() + "] 即将开始", -1, time.getUserId())));
        noticeService.notifyByMailInBatch(notices);
    }
}
