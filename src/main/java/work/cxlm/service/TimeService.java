package work.cxlm.service;

import org.springframework.lang.NonNull;
import work.cxlm.model.dto.TimePeriodSimpleDTO;
import work.cxlm.model.entity.TimePeriod;
import work.cxlm.model.params.TimeParam;
import work.cxlm.model.vo.TimeTableVO;
import work.cxlm.service.base.CrudService;

import java.util.List;

/**
 * 预约时段业务处理接口
 * created 2020/11/16 23:05
 *
 * @author Chiru
 */
public interface TimeService extends CrudService<TimePeriod, Long> {

    /**
     * 获取某活动室指定的时段
     */
    TimeTableVO getTimeTable(@NonNull Integer roomId, Integer week);

    /**
     * 预定时段
     */
    TimePeriodSimpleDTO occupyTimePeriod(@NonNull Long timeId);

    /**
     * 用户解除对时间段的占用
     */
    TimePeriodSimpleDTO cancelTimePeriod(@NonNull Long timeId);

    /**
     * 管理员禁用时段
     */
    TimeTableVO blockBy(@NonNull TimeParam param);

    /**
     * 管理员解除时段禁用
     */
    TimeTableVO clearBy(@NonNull TimeParam param);

    /**
     * 管理员修改时段文本显示
     */
    TimeTableVO changeTextBy(@NonNull TimeParam param);
}
