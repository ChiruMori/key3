package work.cxlm.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import work.cxlm.service.MonitorService;

import java.util.Date;
import java.util.List;

/**
 * created 2021/1/6 14:51
 *
 * @author Chiru
 */
@SpringBootTest
@Slf4j
public class MonitorServiceImplTest {

    @Autowired
    MonitorService monitorService;

    @Test
    public void logDatesTest() {
        List<Date> dates = monitorService.datesHasLog();
        System.out.println("============= 时间列表 ================");
        log.debug(dates.toString());
        System.out.println("============= 时间列表 ================");
    }

    @Test
    public void testDateLog() {
        String openId = "oeEeW5F0IPOaN-8u9r9pvViYPMNc";
        Date today = new Date();
        // 2021/01/03 08:00:00
        Date oneDay = new Date(1609632000000L);
        System.out.println("============= 日志内容 ================");
//        log.debug(monitorService.getDateLog(today, openId));
//        log.debug(monitorService.getDateLog(oneDay, openId));
        System.out.println("============= 日志内容 ================");
    }
}
