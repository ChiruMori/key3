package work.cxlm.utils;

import lombok.NonNull;
import org.springframework.util.Assert;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * 本程序中需要使用的 Date 处理工具方法
 * created 2020/12/6 14:28
 *
 * @author Chiru
 */
public class DateUtils {

    private static final DateUtils INSTANCE = new DateUtils();

    // ================== 构造 ======================

    public DateUtils() {
        calendar = new Calendar.Builder().setInstant(new Date()).build();
    }

    public DateUtils(Date date) {
        calendar = new Calendar.Builder().setInstant(date).build();
    }

    //************************ 静态方法 ***********************

    /**
     * 对日期进行编码，得到纯数字表示（精确到分）
     *
     * @param date 日期对象
     * @return 编码后的数字，如 202012061122
     */
    public static long codeOfDate(Date date) {
        return INSTANCE.of(date).encode();
    }

    /**
     * 获得指定时间周一的日期对象
     */
    public static Date weekStartOf(@NonNull Date date) {
        return INSTANCE.of(date).weekStart().get();
    }

    /**
     * 获得本周一的日期对象
     */
    public static Date weekStartOfNow() {
        return weekStartOf(new Date());
    }

    /**
     * 计算并切换周次
     *
     * @param date   要操作的日期对象
     * @param amount 变化的周次，可以为负数，表示从前
     */
    public static Date changeWeekOf(Date date, int amount) {
        return INSTANCE.of(date).changeWeek(amount).get();
    }

    /**
     * 计算基于开始周，目标周的周次。开始周为第 1 周，开始周前一周为 -1 周
     *
     * @param start  开始周
     * @param target 目标周
     */
    public static int weekNumberOf(Date start, Date target) {
        Long startWeekStamp = weekStartOf(start).getTime();
        Long targetWeekStamp = weekStartOf(target).getTime();
        long weekStamp = 1000L * 3600 * 24 * 7;
        int res = (int) ((targetWeekStamp - startWeekStamp) / weekStamp);
        if (res >= 0) {
            res += 1;
        }
        return res;
    }

    /**
     * 判断指定的日期为星期几
     */
    public static int whatDayIs(Date date) {
        Assert.notNull(date, "date 不能为 null");
        return INSTANCE.of(date).whatDayIsIt();
    }

    public static int whatHourIsNow() {
        return INSTANCE.of(new Date()).getHour();
    }

    // ****************** 实例方法 **********************

    private Calendar calendar;

    /**
     * 生成当前时间的字符串表示，如 12:23
     */
    public String generateTitle() {
        int nowHour = calendar.get(Calendar.HOUR_OF_DAY);
        int nowMinute = calendar.get(Calendar.MINUTE);
        return String.format("%02d:%02d", nowHour, nowMinute);
    }

    /**
     * 获取当前被包装日期的星期
     */
    public int whatDayIsIt() {
        int day = calendar.get(Calendar.DAY_OF_WEEK) - 1;
        if (day == 0) {
            return 7;
        }
        return day;
    }

    /**
     * 获取小时，24 时制
     */
    public int getHour() {
        return calendar.get(Calendar.HOUR_OF_DAY);
    }

    /**
     * 获取当前正在操作的日期对象
     */
    public Date get() {
        return calendar.getTime();
    }

    /**
     * 获取当前正在操作的日期时间戳
     */
    public Long getTime() {
        return calendar.getTimeInMillis();
    }

    /**
     * 对日期进行编码，得到纯数字表示（精确到分）
     */
    public Long encode() {
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH) + 1;
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        // 24 时制
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minus = calendar.get(Calendar.MINUTE);
        return year * 10000_0000L + month * 100_0000L + day * 10000L + hour * 100L + minus;
    }

    public String getFormattedTime() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy.MM.dd HH:mm");
        return sdf.format(calendar.getTime());
    }

    @Override
    public String toString() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy.MM.dd HH:mm:ss.SSS");
        return sdf.format(calendar.getTime());
    }

    // ------------------ 链式调用 --------------------------------------

    /**
     * 更换正在操作的日期对象
     */
    public DateUtils of(Date date) {
        calendar = new Calendar.Builder().setInstant(date).build();
        return this;
    }

    public DateUtils weekStart() {
        // 代码中每周以周日开始，周六为 7
        int nowWeek = calendar.get(Calendar.DAY_OF_WEEK);
        if (nowWeek == Calendar.SUNDAY) {
            calendar.add(Calendar.DAY_OF_MONTH, -6);
        } else {
            nowWeek -= 2;
            calendar.add(Calendar.DAY_OF_MONTH, -nowWeek);
        }
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        return this;
    }

    /**
     * 切换周次
     *
     * @param amount 要切换的周数，可以为负数
     */
    public DateUtils changeWeek(int amount) {
        calendar.add(Calendar.WEEK_OF_MONTH, amount);
        return this;
    }

    public DateUtils tomorrow() {
        calendar.add(Calendar.DAY_OF_MONTH, 1);
        return this;
    }

    public DateUtils yesterday() {
        calendar.add(Calendar.DAY_OF_MONTH, -1);
        return this;
    }

    /**
     * 设置小时
     */
    public DateUtils setHour(int target) {
        calendar.set(Calendar.HOUR_OF_DAY, target);
        return this;
    }

    public DateUtils setMinute(int target) {
        calendar.set(Calendar.MINUTE, target);
        return this;
    }

    /**
     * 下一个整点
     */
    public DateUtils nextTopHour() {
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        calendar.add(Calendar.HOUR_OF_DAY, 1);
        return this;
    }

    /**
     * 向前移动一个小时
     */
    public DateUtils lastHour() {
        calendar.add(Calendar.HOUR_OF_DAY, -1);
        return this;
    }

    /**
     * 向前移动一个小时
     */
    public DateUtils nextHour() {
        calendar.add(Calendar.HOUR_OF_DAY, 1);
        return this;
    }
}
