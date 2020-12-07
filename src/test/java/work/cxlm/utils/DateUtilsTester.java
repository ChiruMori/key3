package work.cxlm.utils;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Date;

/**
 * created 2020/12/6 14:39
 *
 * @author Chiru
 */
@Slf4j
public class DateUtilsTester {

    @Test
    public void dateCodeTest() {
        long l = DateUtils.codeOfDate(new Date(1607237190000L));
        assertEquals(l, 202012061446L);

        l = DateUtils.codeOfDate(new Date(1407012390000L));
        assertEquals(l, 201408030446L);

        l = DateUtils.codeOfDate(new Date(0));
        assertEquals(l, 197001010800L);
    }

    @Test
    public void weekStartTest() {
        Date date20201206 = new Date(1607263861045L);
        Date date20201130 = new Date(1606665600000L);

        Date res20201206 = DateUtils.weekStartOf(date20201206);
        Date res20201130 = DateUtils.weekStartOf(date20201130);

        assertEquals(date20201130.getTime(), res20201206.getTime());
        assertEquals(date20201130.getTime(), res20201130.getTime());
    }

    @Test
    public void weekChangeTest() {
        Date date20201129 = new Date(1606579200000L);
        Date date20201206 = new Date(1607184000000L);
        Date date20201213 = new Date(1607788800000L);

        assertEquals(DateUtils.changeWeekOf(date20201206, -1).getTime(), date20201129.getTime());
        assertEquals(DateUtils.changeWeekOf(date20201206, 1).getTime(), date20201213.getTime());
    }

    @Test
    public void weekNumberTest() {
        Date date20201202 = new Date(1606838400000L);
        Date date20201218 = new Date(1608220800000L);
        Date date20201221 = new Date(1608480000000L);
        Date date20201227 = new Date(1608998400000L);
        Date date20210107 = new Date(1609948800000L);

        assertEquals(DateUtils.weekNumberOf(date20201218, date20201218), 1);
        assertEquals(DateUtils.weekNumberOf(date20201218, date20201202), -2);
        assertEquals(DateUtils.weekNumberOf(date20201218, date20201227), 2);
        assertEquals(DateUtils.weekNumberOf(date20201218, date20201221), 2);
        assertEquals(DateUtils.weekNumberOf(date20201218, date20210107), 4);
    }

    @Test
    public void whatDayTest() {
        Date date20201213 = new Date(1607788800000L);  // 周日
        Date date20210107 = new Date(1609948800000L);  // 周四
        Date date20201130 = new Date(1606665600000L);  // 周一
        Date date20201205 = new Date(1607097600000L);  // 周六

        assertEquals(DateUtils.whatDayIs(date20201213), 7);
        assertEquals(DateUtils.whatDayIs(date20210107), 4);
        assertEquals(DateUtils.whatDayIs(date20201130), 1);
        assertEquals(DateUtils.whatDayIs(date20201205), 6);
    }
}
