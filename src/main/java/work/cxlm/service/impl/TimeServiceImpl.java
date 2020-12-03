package work.cxlm.service.impl;

import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import work.cxlm.cache.AbstractStringCacheStore;
import work.cxlm.config.QfzsProperties;
import work.cxlm.exception.AuthenticationException;
import work.cxlm.exception.BadRequestException;
import work.cxlm.exception.ForbiddenException;
import work.cxlm.model.entity.Club;
import work.cxlm.model.entity.Room;
import work.cxlm.model.entity.TimePeriod;
import work.cxlm.model.entity.User;
import work.cxlm.model.params.TimeParam;
import work.cxlm.repository.RoomRepository;
import work.cxlm.repository.TimeRepository;
import work.cxlm.security.context.SecurityContextHolder;
import work.cxlm.service.*;
import work.cxlm.service.base.AbstractCrudService;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static work.cxlm.model.enums.TimeState.*;


/**
 * @program: myfont
 * @author: beizi
 * @create: 2020-11-20 12:52
 * @application :
 * @Version 1.0
 **/
@Slf4j
@Service
public class TimeServiceImpl extends AbstractCrudService<TimePeriod, Integer> implements TimeService {

    private RoomService roomService;
    private UserService userService;
    private TimeService timeService;
    private BelongService belongService;


    @Autowired
    public void setBelongService(BelongService belongService) {
        this.belongService = belongService;
    }

    @Autowired
    public void setTimeService(TimeService timeService) {
        this.timeService = timeService;
    }

    @Autowired
    public void setRoomService(RoomService roomService) {
        this.roomService = roomService;
    }

    @Autowired
    public void setUserService(UserService userService) {
        this.userService = userService;
    }

    private final RoomRepository roomRepository;
    private final QfzsProperties qfzsProperties;
    private final ApplicationEventPublisher eventPublisher;
    private final AbstractStringCacheStore cacheStore;
    private final JoiningService joiningService;
    private final TimeRepository timeRepository;


    public TimeServiceImpl(RoomRepository roomRepository,
                           QfzsProperties qfzsProperties,
                           ApplicationEventPublisher eventPublisher,
                           AbstractStringCacheStore cacheStore,
                           JoiningService joiningService,
                           TimeRepository timeRepository
    ) {
        super(timeRepository);
        this.roomRepository = roomRepository;
        this.qfzsProperties = qfzsProperties;
        this.eventPublisher = eventPublisher;
        this.cacheStore = cacheStore;
        this.joiningService = joiningService;
        this.timeRepository = timeRepository;

    }


    /*
     *
     *
     * @description: 判断进入预约模块的成员信息是否过期
     * @param null
     * @return:
     * @time: 2020/11/21 19:47
     */
    public @NonNull User getUser() {
        return SecurityContextHolder.getCurrentUser().orElseThrow(
                () -> new AuthenticationException("用户登录凭证无效"));
    }

    public @NonNull Room getRoom(@NonNull Integer roomId) {
        /*
         *
         *
         * @description:  通过roomId找到活动室
         * @param roomId  活动室编号
         * @return: work.cxlm.model.entity.Room
         * @time: 2020/11/21 21:39
         */
        return roomRepository.findById(roomId).orElseThrow(() -> new BadRequestException("找不到给定编号的活动室，请输入正确的活动室编号"));
    }

    public @NonNull TimePeriod getTimePeriod(@NonNull Integer timeId) {
        /*
         *
         *
         * @description: 通过时间段编号找到时间段
         * @param timeId
         * @return: work.cxlm.model.entity.TimePeriod
         * @time: 2020/11/21 21:38
         */
        return timeRepository.findById(timeId).orElseThrow(() -> new BadRequestException("找不到该时间段，请输入正确的时间段编号"));
    }

    public @NonNull Room getRoomByTimeId(@NonNull Integer timeId) {
        /*
         *
         *
         * @description:  通过timeId确定活动室
         * @param timeId
         * @return: work.cxlm.model.entity.Room
         * @time: 2020/11/21 21:38
         */
        TimePeriod timePeriod = timeRepository.findById(timeId).orElseThrow(() -> new BadRequestException("找不到该时间段，请输入正确的时间段编号"));
        Integer roomId = timePeriod.getRoomId();
        return roomRepository.findById(roomId).orElseThrow(() -> new BadRequestException("找不到给定编号的活动室，请输入正确的活动室编号"));

    }

    @Override
    /*
     *
     *
     * @description:
     * @param roomId  活动室的id
     * @param timeId  活动室某一时间段的id
     * @return: work.cxlm.model.entity.TimePeriod  返回时间段
     * @time: 2020/11/21 16:48
     */
    public @NonNull TimePeriod querryRoomTime(@NonNull Integer timeId) {
        TimePeriod timePeriod = timeRepository.findById(timeId).orElseThrow();
        return timePeriod;

    }

    @Override
    public List listAllPeriod(@NonNull Integer roomId) {
        List<TimePeriod> allByRoomId = timeRepository.findAllByRoomId(roomId);
        List<TimePeriod> timePeriods = new ArrayList<>();
        for (TimePeriod timePeriod : allByRoomId) {
            if (timePeriod.getRoomId().equals(roomId)) {
                timePeriods.add(timePeriod);
            }
        }
        return listAllWeek(timePeriods);
    }

    public List listAllWeek(List<TimePeriod> timePeriods){
        ArrayList list=new ArrayList();
        for(int i = 0;i<7;i++){

                list.add(timePeriods);

        }
            return list;
    }

    @Override
    /*
     *
     *
     * @description: 成员预订活动室某一个时间段
     * @param roomId
     * @param timeId
     * @return: work.cxlm.model.entity.TimePeriod
     * @time: 2020/11/21 19:46
     */
    public void orderRoom(@NonNull Integer timeId) {

        @NonNull User user = getUser();
        if (user != null) {
            @NonNull TimePeriod timePeriod = timeRepository.findById(timeId).orElseThrow();
            if (timePeriod != null) {
                if (timePeriod.getTimeState().isIdle()) {  //是空闲
                    timePeriod.setTimeState(Time_ORDER);  // 活动室状态变为已经被预约
                    timePeriod.setUserId(user.getId());
                    updateTime(timePeriod);
                    log.info("[{}]--预约--[{}]----[{}]<<<>>>[{}] ", user.getRealName(), timePeriod.getId(), timePeriod.getStartTime(), timePeriod.getTimeState());
                } else {
                    log.debug("该时间段已经被预定，不能预定!");
                }
            }
        } else {
            throw new ForbiddenException("登录信息已丢失!");
        }

    }

    @Override
    /*
     *
     *
     * @description: 成员关注某一个活动室的时间段
     * @param roomId
     * @param timeId
     * @return: work.cxlm.model.entity.TimePeriod
     * @time: 2020/11/21 19:46
     */
    public TimePeriod attRoom(@NonNull Integer timeId) {
        @NonNull User user = getUser();
        if (user != null) {
            @NonNull TimePeriod timePeriod = querryRoomTime(timeId);
            timePeriod.setTimeState(Time_ATT);
            updateTime(timePeriod);
            log.info("[{}]--关注----活动室的--[{}]<<<>>>[{}] ", user.getRealName(), timePeriod.getStartTime(), timePeriod.getEndTime());
            return timePeriod;
        }
        return null;

    }

    @Override
    /*
     *
     *
     * @description: 成员取消预订活动室的某一个时间段
     * @param roomId
     * @param timeId
     * @return: work.cxlm.model.entity.TimePeriod
     * @time: 2020/11/21 19:45
     */
    public TimePeriod noOrderRoom(@NonNull Integer timeId) {

        @NonNull User user = getUser();
        @NonNull TimePeriod timePeriod = querryRoomTime(timeId);
        timePeriod.setTimeState(Time_IDLE);
        timePeriod.setUserId(null);
        updateTime(timePeriod);
        log.debug("[{}]--取消预约了--[{}]--时间段", user.getRealName(), timeId);
        return timePeriod;

    }

    @Override
    /*
     *
     *
     * @description: 成员取消时间段的关注
     * @param roomId
     * @param timeId
     * @return: work.cxlm.model.entity.TimePeriod
     * @time: 2020/11/21 19:45
     */
    public TimePeriod noAttRoom(@NonNull Integer timeId) {

        getUser();
        @NonNull TimePeriod timePeriod = querryRoomTime(timeId);
        timePeriod.setTimeState(Time_ORDER);
        updateTime(timePeriod);
        timePeriod.setUserId(null);
        log.debug("123");
        return timePeriod;

    }


    @Override
    /*
     *
     *
     * @description:  更新房间信息
     * @param roomId
     * @return: void
     * @time: 2020/11/21 16:50
     */
    public void updateTime(@NonNull TimePeriod timePeriod) {
        timeRepository.save(timePeriod);
        log.debug("");
    }


    @Override
    /*
     *
     *
     * @description: 管理员禁用时间段
     * @param timeId
     * @return: boolean
     * @time: 2020/11/23 22:02
     */
    public boolean adminStop(@NonNull Integer timeId) {
        @NonNull User user = getUser();
        TimePeriod timePeriod = timeRepository.findById(timeId).orElseThrow();
        if (user.getRole().isAdminRole()) {
            timePeriod.getTimeState().setBan();
            log.debug("管理员--[{}]--禁用了-[{}]", user.getRealName(), timeId);
            return true;
        } else {
            log.debug("管理员已经禁用了--[{}]", timePeriod.getId());
        }
        return false;
    }

    @Override
    /*
     *
     *
     * @description: 管理员恢复时间段使用
     * @param timeId
     * @return: boolean
     * @time: 2020/11/23 22:02
     */
    public boolean adminAllow(@NonNull Integer timeId) {
        @NonNull User user = getUser();
        TimePeriod timePeriod = timeRepository.findById(timeId).orElseThrow();
        if (user.getRole().isAdminRole()) {
            if (timePeriod.getTimeState().isStop()) {
                timePeriod.getTimeState().setTime_IDLE();
                updateTime(timePeriod);
                log.debug("管理员--[{}]恢复了-[{}]", user.getRealName(), timeId);
                return true;
            }
        }
        return false;
    }

    @Override
    public @NonNull Optional<Room> findRoom(@NonNull TimeParam timeParam) {
        return roomService.findByRoomId(timeParam.getRoomId());
    }

    @Override
    public Optional<User> findUser(@NonNull TimeParam timeParam) {
        return userService.getByStudentNo(timeParam.getStudentNo());
    }

    @Override
    public void orderTimePeriod(@NonNull TimeParam timeParam) {
        Room room = findRoom(timeParam).orElseThrow();
        User user = findUser(timeParam).orElseThrow();
        @NonNull TimePeriod timePeriod = timeService.querryRoomTime(timeParam.getId());
        List<Club> clubs = userService.userOrderRoom(user, room);
        if (clubs != null) {
            if (timePeriod.getTimeState().isIdle()) {
                timeService.orderRoom(timeParam.getId());
            }
        }
    }

    @Override
    public void noOrderTimePeriod(@NonNull TimeParam timeParam) {
        Room room = findRoom(timeParam).orElseThrow();
        User user = findUser(timeParam).orElseThrow();
        List<Club> clubs = userService.userOrderRoom(user, room);
        @NonNull TimePeriod timePeriod = timeService.querryRoomTime(timeParam.getId());
        if (clubs != null) {
            int a = timeService.querryRoomTime(timeParam.getId()).getUserId();
            if (a == user.getId()) {
                if (timePeriod.getTimeState().isOrder()) {
                    timeService.noOrderRoom(timeParam.getId());
                }
            }
        }
    }

    @Override
    public void attTimePeriod(@NonNull TimeParam timeParam) {
        Room room = findRoom(timeParam).orElseThrow();
        User user = findUser(timeParam).orElseThrow();
        List<Club> clubs = userService.userOrderRoom(user, room);
        @NonNull TimePeriod timePeriod = timeService.querryRoomTime(timeParam.getId());
        if (clubs != null) {
            if (timePeriod.getTimeState().isOrder()) {
                timeService.attRoom(timeParam.getId());
            }
        }
    }

    public void banTimePeriod(@NonNull TimeParam timeParam) {

        User user = findUser(timeParam).orElseThrow();
        if (user.getRole().isAdminRole()) {
            timeService.adminStop(timeParam.getId());
        }
    }

    public void allowTimePeriod(@NonNull TimeParam timeParam) {
        User user = findUser(timeParam).orElseThrow();
        if (user.getRole().isAdminRole()) {
            timeService.adminAllow(timeParam.getId());
        }
    }

}
