package work.cxlm.service;

import lombok.NonNull;
import work.cxlm.model.entity.Room;
import work.cxlm.model.entity.TimePeriod;
import work.cxlm.model.entity.User;
import work.cxlm.model.params.TimeParam;
import work.cxlm.service.base.CrudService;

import java.util.List;
import java.util.Optional;

/**
 * 预约时段业务处理接口
 * created 2020/11/16 23:05
 *
 * @author Chiru
 */
public interface TimeService extends CrudService<TimePeriod, Integer> {

    //根据前端进行查询活动室
    @NonNull Optional<Room> findRoom(@NonNull TimeParam timeParam);
    //查找活动室某一时间段
    @NonNull
    TimePeriod querryRoomTime(@NonNull Integer timeId);

    //活动室的所有时间段
    List<TimePeriod> listAllPeriod(@NonNull Integer roomId);

    // 预定活动室某一时间段

    void orderRoom(@NonNull Integer timeId);

    //关注活动室某一时间段
    TimePeriod attRoom(@NonNull Integer timeId);

    //取消预订活动室某一时间段
    TimePeriod noOrderRoom(@NonNull Integer timeId);

    //取消关注活动室某一时间段
    TimePeriod noAttRoom(@NonNull Integer timeId);

    //更新活动室使用信息
    void updateTime(@NonNull TimePeriod timePeriod);

    //管理员禁用时间段
    boolean adminStop(@NonNull Integer timeId);

    //管理员恢复时间段
    boolean adminAllow(@NonNull Integer timeId);

    //预定时间段
    void orderTimePeriod(@NonNull TimeParam timeParam);

    void noOrderTimePeriod(@NonNull TimeParam timeParam);

    void attTimePeriod(@NonNull TimeParam timeParam);

    Optional<User> findUser(@NonNull TimeParam timeParam);




}
