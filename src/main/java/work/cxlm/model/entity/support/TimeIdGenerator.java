package work.cxlm.model.entity.support;

import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.id.IdentityGenerator;
import org.springframework.lang.NonNull;
import org.springframework.util.Assert;
import work.cxlm.exception.ServiceException;
import work.cxlm.model.entity.TimePeriod;
import work.cxlm.utils.DateUtils;

import java.io.Serializable;
import java.util.Calendar;
import java.util.Date;

/**
 * 时段 ID 编码策略的实现，所有使用该编码进行转化、获取信息的方法均在这里定义
 * 从 ID 中获取信息时，应该调用本类方法，而不在其他地方手动转化，如果编码规则发生改变将影响较小
 * created 2020/12/6 10:56
 *
 * @author Chiru
 */
public class TimeIdGenerator extends IdentityGenerator {

    public static final long MAX_ROOM_ID = 10000L;

    public static int decodeHourFromId(Long id) {
        return (int) (id % 10000_0000 / 100_0000);
    }

    @Override
    public Serializable generate(SharedSessionContractImplementor s, Object obj) {
        if (obj == null) {
            throw new NullPointerException("实体类不能为 null");
        }
        if (obj instanceof TimePeriod) {
            return DateUtils.codeOfDate(((TimePeriod) obj).getStartTime());
        }
        return super.generate(s, obj);
    }

    /**
     * 使用日期对象、活动室 ID 得到时间段的 ID
     */
    public static long encodeId(@NonNull Date date, int roomId) {
        Assert.notNull(date, "date 不能为 null");
        if (roomId >= MAX_ROOM_ID) {
            throw new ServiceException("错误！roomId 超出设定范围，roomId 为：" + roomId + "，请联系管理员报告此问题。");
        }

        return DateUtils.codeOfDate(date) * MAX_ROOM_ID + roomId;
    }

    public static long encodeId(@NonNull DateUtils du, int roomId) {
        return du.encode() * MAX_ROOM_ID + roomId;
    }

    public static Date decodeIdToDate(@NonNull Long id) {
        Assert.notNull(id, "id 不能为 null");
        id /= MAX_ROOM_ID;
        int year = (int) (id / 10000_0000L);
        id %= 10000_0000L;
        int month = (int) (id / 100_0000L) - 1;
        id %= 100_0000L;
        int day = (int) (id / MAX_ROOM_ID);
        id %= MAX_ROOM_ID;
        int hour = (int) (id / 100L);
        id %= 100L;
        return new Calendar.Builder().
                setDate(year, month, day).
                setTimeOfDay(hour, (int) (long) (id), 0).
                build().getTime();
    }

    /**
     * 获得前一时段的时间 ID，注意，不会跨越日期
     */
    public static Long previousTimeId(Long timeId) {
        return timeId - 100_0000L;
    }

    /**
     * 从 ID 中得到 roomId
     */
    public static int getRoomId(Long timeId) {
        return (int) (timeId % MAX_ROOM_ID);
    }
}
