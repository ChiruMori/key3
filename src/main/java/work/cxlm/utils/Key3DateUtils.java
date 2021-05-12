package work.cxlm.utils;

import org.apache.commons.lang3.time.DateUtils;
import org.springframework.lang.NonNull;
import org.springframework.util.Assert;

import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.TimeUnit;

/**
 * created 2020/11/1 15:52
 *
 * @author johnniang
 * @author cxlm
 */
public class Key3DateUtils {

    private Key3DateUtils() {
    }

    /**
     * 获取当前时间
     */
    @NonNull
    public static Date now() {
        return new Date();
    }

    /**
     * 将 Date 实例转化为 Calendar 实例
     */
    @NonNull
    public static Calendar convertTo(@NonNull Date date) {
        Assert.notNull(date, "date 不能为 null");

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        return calendar;
    }

    /**
     * 增加时间
     *
     * @param date     被计算的 Date 实例
     * @param time     增加的时间，不能小于零
     * @param timeUnit 时间单位
     * @return 计算后的时间
     */
    public static Date add(@NonNull Date date, long time, @NonNull TimeUnit timeUnit) {
        Assert.notNull(date, "Date 实例不能为 null");
        Assert.isTrue(time >= 0, "增加的时间不能小于 0");
        Assert.notNull(timeUnit, "时间单位不能为 null");

        Date result;

        int timeIntValue;

        if (time > Integer.MAX_VALUE) {
            timeIntValue = Integer.MAX_VALUE;
        } else {
            timeIntValue = Long.valueOf(time).intValue();
        }

        // 根据时间单位计算时间
        switch (timeUnit) {
            case DAYS:
                result = DateUtils.addDays(date, timeIntValue);
                break;
            case HOURS:
                result = DateUtils.addHours(date, timeIntValue);
                break;
            case MINUTES:
                result = DateUtils.addMinutes(date, timeIntValue);
                break;
            case SECONDS:
                result = DateUtils.addSeconds(date, timeIntValue);
                break;
            case MILLISECONDS:
                result = DateUtils.addMilliseconds(date, timeIntValue);
                break;
            default:
                result = date;
        }
        return result;
    }
}
