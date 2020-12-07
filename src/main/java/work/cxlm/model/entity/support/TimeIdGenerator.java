package work.cxlm.model.entity.support;

import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.id.IdentityGenerator;
import org.springframework.lang.NonNull;
import org.springframework.util.Assert;
import work.cxlm.model.entity.TimePeriod;
import work.cxlm.utils.DateUtils;

import java.io.Serializable;
import java.util.Calendar;
import java.util.Date;

/**
 * 时段 ID 编码策略的实现
 * created 2020/12/6 10:56
 *
 * @author Chiru
 */
public class TimeIdGenerator extends IdentityGenerator {

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

        return DateUtils.codeOfDate(date) * 10000L + roomId;
    }

    public static long encodeId(@NonNull DateUtils du, int roomId) {
        return du.encode() * 10000L + roomId;
    }

    public static Date decodeIdToDate(@NonNull Long id) {
        Assert.notNull(id, "id 不能为 null");
        id /= 10000L;
        int year = (int) (id / 10000_0000L);
        id %= 10000_0000L;
        int month = (int) (id / 100_0000L) - 1;
        id %= 100_0000L;
        int day = (int) (id / 10000L);
        id %= 10000L;
        int hour = (int) (id / 100L);
        id %= 100L;
        return new Calendar.Builder().
                setDate(year, month, day).
                setTimeOfDay(hour, (int) (long) (id), 0).
                build().getTime();
    }
}
