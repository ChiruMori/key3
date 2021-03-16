package work.cxlm.service;

import org.springframework.lang.NonNull;
import org.springframework.transaction.annotation.Transactional;
import work.cxlm.model.dto.TimePeriodSimpleDTO;
import work.cxlm.model.entity.Club;
import work.cxlm.model.entity.Room;
import work.cxlm.model.entity.TimePeriod;
import work.cxlm.model.entity.User;
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
     *
     * @param roomId 指定的活动室 id
     * @param week   基于当前周的周次
     * @return 时间表格视图对象
     */
    TimeTableVO getTimeTable(@NonNull Integer roomId, Integer week);

    /**
     * 预定时段
     *
     * @param timeId 时间段 ID
     * @return 时段 DTO
     */
    TimePeriodSimpleDTO occupyTimePeriod(@NonNull Long timeId);

    /**
     * 用户解除对时间段的占用
     *
     * @param timeId 时段 ID
     * @return 时段 DTO
     */
    TimePeriodSimpleDTO cancelTimePeriod(@NonNull Long timeId);

    /**
     * 管理员禁用时段
     *
     * @param param 前端时段参数
     * @return 时间表视图对象
     */
    TimeTableVO blockBy(@NonNull TimeParam param);

    /**
     * 管理员解除时段禁用
     *
     * @param param 前端时段参数
     * @return 时间表视图对象
     */
    TimeTableVO clearBy(@NonNull TimeParam param);

    /**
     * 管理员修改时段文本显示
     *
     * @param param 前端时段参数
     * @return 时间表视图对象
     */
    TimeTableVO changeTextBy(@NonNull TimeParam param);

    /**
     * 删除用户的全部预定信息
     *
     * @param userId 用户 id
     */
    void deleteByUserId(@NonNull Integer userId);

    /**
     * 获取某社团指定周的活动室使用率
     *
     * @param weekNum 周次编号，当前周为 0
     * @param club 活动室
     * @return 指定周周次的活动室使用率
     */
    float getWeekUsage(int weekNum, @NonNull Club club);

    /**
     * 删除用户在指定社团未来的预约
     *
     * @param targetUser 目标用户
     * @param targetClub 指定的社团
     */
    void deleteUserFutureTime(@NonNull User targetUser, @NonNull Club targetClub);

    /**
     * 删除在活动室可用时间范围外的时段
     *
     * @param targetRoom 目标活动室
     */
    @Transactional(rollbackFor = Exception.class)
    void removeOutTime(@NonNull Room targetRoom);

    /**
     * 查询在指定时间内全部的有效时段
     *
     * @param minId 开始时间的 ID
     * @param maxId 结束时间的 ID
     * @return 时间区间内的有效时段
     */
    List<TimePeriod> listAllTimeByIdBetween(@NonNull Long minId, @NonNull Long maxId);
}
