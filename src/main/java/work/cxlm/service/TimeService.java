package work.cxlm.service;

import lombok.NonNull;
import work.cxlm.model.dto.TimePeriodSimpleDTO;
import work.cxlm.model.entity.Room;
import work.cxlm.model.entity.TimePeriod;
import work.cxlm.model.entity.User;
import work.cxlm.model.params.TimeParam;
import work.cxlm.model.vo.TimeTableVO;
import work.cxlm.service.base.CrudService;

import java.util.List;
import java.util.Optional;

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
    void occupyTimePeriod(@NonNull Long timeId);

    /**
     * 用户解除对时间段的占用
     */
    void cancelTimePeriod(@NonNull Long timeId);

}
