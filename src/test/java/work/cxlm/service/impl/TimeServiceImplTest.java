package work.cxlm.service.impl;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import work.cxlm.model.entity.TimePeriod;
import work.cxlm.service.TimeService;

import javax.annotation.Resource;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class TimeServiceImplTest {

    private TimeService timeService;

    @Autowired
    public void setTimeService(TimeService timeService) {
        this.timeService = timeService;
    }

    @Test
    void querryRoomTime() {
    }

    @Test
    void orderRoom() {
    }

    @Test
    void attRoom() {
    }

    @Test
    void noOrderRoom() {
    }

    @Test
    void noAttRoom() {
    }

    @Test
    void updateRoom() {
    }

    @Test
    void adminStop() {
    }

    @Test
    void adminAllow() {
    }
    @Test
    void listAll() {
        List<TimePeriod> timePeriods = timeService.listAllPeriod(1);
        for (TimePeriod timePeriod : timePeriods) {
            System.out.println(timePeriod.toString());
        }
    }
}