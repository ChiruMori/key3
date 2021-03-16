package work.cxlm.task;

import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import work.cxlm.model.entity.*;
import work.cxlm.model.entity.support.TimeIdGenerator;
import work.cxlm.model.enums.NoticeType;
import work.cxlm.service.*;
import work.cxlm.utils.DateUtils;
import work.cxlm.utils.ServiceUtils;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * created 2021/3/16 23:15
 * 每日用户时长统计的跑批任务
 *
 * @author Chiru
 */
@Component
@EnableAsync
@EnableScheduling
@Slf4j
public class DailyTotalTask {

    private final BelongService belongService;
    private final JoiningService joiningService;
    private final RoomService roomService;
    private final TimeService timeService;

    public DailyTotalTask(BelongService belongService,
                          JoiningService joiningService,
                          RoomService roomService,
                          TimeService timeService) {
        this.belongService = belongService;
        this.joiningService = joiningService;
        this.roomService = roomService;
        this.timeService = timeService;
    }

    /**
     * 每天 00:00:30 执行一次，统计用户使用时长
     * 秒 分 时 日 月 星期
     */
    @Async
    @Scheduled(cron = "30 0 0 * * ?")
    public void beforeTimeStart() {
        DateUtils du = new DateUtils().setHour(0).setMinute(0);
        long maxTimeIdOfYesterday = TimeIdGenerator.encodeId(du, 0);
        long minTimeIdOfYesterday = TimeIdGenerator.encodeId(du.yesterday(), 0);
        List<TimePeriod> validTimePeriodsInYesterday = timeService.listAllTimeByIdBetween(minTimeIdOfYesterday, maxTimeIdOfYesterday);

        if (CollectionUtils.isEmpty(validTimePeriodsInYesterday)) {
            log.info("日结算完毕：昨日有效预约数为 0");
            return;
        }

        // 整理活动室、社团映射关系
        List<Belong> belongList = belongService.listAll();
        Map<Integer, Integer> roomId2ClubIdMap = ServiceUtils.convertToMap(belongList, belong -> belong.getId().getRoomId(), belong -> belong.getId().getClubId());

        List<Integer> userIds = validTimePeriodsInYesterday.stream().map(TimePeriod::getUserId).collect(Collectors.toList());
        List<Joining> joiningOfYesterday = joiningService.listAllJoiningByUserIdIn(userIds);

        int[] counter = new int[]{0};
        validTimePeriodsInYesterday.forEach(timePeriod -> {
            Integer targetClubId = roomId2ClubIdMap.get(TimeIdGenerator.getRoomId(timePeriod.getId()));
            joiningOfYesterday.forEach(joining -> {
                // 为该活动室归属的社团与当前用户的 Joining 实例的 total 字段加 1
                if (joining.getId().getClubId().equals(targetClubId)) {
                    joining.setTotal(joining.getTotal() + 1);
                    counter[0]++;
                }
            });
        });
        // 变动写入数据库
        joiningService.updateInBatch(joiningOfYesterday);
        log.info("{} 日的时段统计完毕，共统计有效预约 {} 条，用户统计时长总增长 {} 时",
                du.getFormattedTime(), validTimePeriodsInYesterday.size(), counter[0]);
    }
}
