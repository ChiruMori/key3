package work.cxlm.task;

import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import work.cxlm.service.LogService;
import work.cxlm.utils.DateUtils;

import java.util.Date;

/**
 * created 2022/11/5 00:12
 * 每周清除 3 个月前的日志
 *
 * @author Chiru
 */
@Component
@EnableAsync
@EnableScheduling
@Slf4j
public class LogCleanTask {

    private final LogService logService;

    public LogCleanTask(LogService logService) {
        this.logService = logService;
    }


    /**
     * 周日三点执行一次
     * 秒 分 时 日 月 星期
     */
    @Async
    @Scheduled(cron = "0 0 3 ? * SUN")
    public void cleanSystemLog() {
        int affectRows = logService.cleanAllBeforeDate(DateUtils.changeMonthOf(new Date(), -3));
        log.info("已清除日志 {} 条", affectRows);
    }
}
