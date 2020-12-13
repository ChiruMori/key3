package work.cxlm.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import work.cxlm.cache.lock.CacheLock;
import work.cxlm.exception.BadRequestException;
import work.cxlm.exception.ForbiddenException;
import work.cxlm.exception.NotFoundException;
import work.cxlm.model.dto.TimePeriodSimpleDTO;
import work.cxlm.model.entity.Room;
import work.cxlm.model.entity.TimePeriod;
import work.cxlm.model.entity.User;
import work.cxlm.model.entity.support.TimeIdGenerator;
import work.cxlm.model.params.TimeParam;
import work.cxlm.model.properties.RuntimeProperties;
import work.cxlm.model.vo.TimeTableVO;
import work.cxlm.repository.TimeRepository;
import work.cxlm.security.context.SecurityContextHolder;
import work.cxlm.service.*;
import work.cxlm.service.base.AbstractCrudService;
import work.cxlm.utils.DateUtils;
import work.cxlm.utils.ServiceUtils;

import java.util.*;
import java.util.function.Consumer;

import static work.cxlm.model.enums.TimeState.*;

/**
 * @author beizi
 * @author Chiru
 * <p>
 * create: 2020-11-20 12:52
 */
@Slf4j
@Service
public class TimeServiceImpl extends AbstractCrudService<TimePeriod, Long> implements TimeService {

    private RoomService roomService;
    private UserService userService;
    private BelongService belongService;


    private final TimeRepository timeRepository;
    private final OptionService optionService;

    @Autowired
    public void setBelongService(BelongService belongService) {
        this.belongService = belongService;
    }

    @Autowired
    public void setRoomService(RoomService roomService) {
        this.roomService = roomService;
    }

    @Autowired
    public void setUserService(UserService userService) {
        this.userService = userService;
    }


    public TimeServiceImpl(TimeRepository timeRepository,
                           OptionService optionService) {
        super(timeRepository);
        this.timeRepository = timeRepository;
        this.optionService = optionService;
    }

    // ***************** Private ***********************************

    // 获得指定周、活动室的全部时间段
    private List<TimePeriod> getWeekTimePeriods(@NonNull Room room, @NonNull Integer week) {
        Assert.notNull(room, "room 不能为 null");
        Assert.notNull(week, "请求的周次不能为 null");

        Integer roomId = room.getId();
        DateUtils du = new DateUtils(new Date());
        // 移动到新的一周
        if (du.whatDayIsIt() == 7 && du.getHour() >= room.getEndHour()) {
            du.tomorrow();
        }
        du.weekStart().changeWeek(week + 1);
        Long endTimeId = TimeIdGenerator.encodeId(du, roomId);
        Long startTimeId = TimeIdGenerator.encodeId(du.changeWeek(-1), roomId);
        return timeRepository.findAllByRoomIdAndIdBetween(roomId, startTimeId, endTimeId);
    }

    // 构建指定活动室、周次、用户的预约表格
    private TimeTableVO buildTable(@NonNull Room targetRoom, @NonNull Integer week, User nowUser) {
        // 获得活动室指定周的全部时段
        List<TimePeriod> weekTimePeriods = getWeekTimePeriods(targetRoom, week);

        // 整理数据，并标记自己占用的时间段
        Map<Long, TimePeriod> timePeriodMap = ServiceUtils.convertToMap(weekTimePeriods, TimePeriod::getId, time -> {
            if (Objects.equals(time.getUserId(), nowUser.getId())) {
                time.setState(MINE);
            }
            return time;
        });
        LinkedList<List<TimePeriodSimpleDTO>> timeTable = new LinkedList<>();
        LinkedList<String> timeTitleList = new LinkedList<>();
        int startHour = targetRoom.getStartHour();
        int endHour = targetRoom.getEndHour();

        DateUtils du = new DateUtils(new Date());
        boolean nextFlag = false;
        // 周日，而且超过了活动室的当日关闭时间
        if (du.whatDayIsIt() == 7 && du.getHour() >= targetRoom.getEndHour()) {
            week += 1;
            nextFlag = true;
        }
        long nowTimeId = TimeIdGenerator.encodeId(du.lastHour(), 0);  // 获得当前时间点的 ID
        du.changeWeek(week).weekStart();  // 移动到指定周周一
        for (int i = startHour; i < endHour; i++) {
            // 生成行标题
            du.setHour(i + 1);
            String endTimeTitle = du.generateTitle();
            String startTimeTitle = du.setHour(i).generateTitle();
            timeTitleList.add(String.format("%s %2s", startTimeTitle, endTimeTitle));

            // 行数据整理
            LinkedList<TimePeriodSimpleDTO> hourRow = new LinkedList<>();
            for (int j = 0; j < 7; j++) {
                Long timeId = TimeIdGenerator.encodeId(du, targetRoom.getId());

                TimePeriod nowTime;
                // 数据库中存在该 ID，将其放入时间表格
                if (timePeriodMap.containsKey(timeId)) {
                    nowTime = timePeriodMap.get(timeId);
                } else {
                    // 数据库中不存在，生成占位时间段实例
                    nowTime = new TimePeriod(timeId, du.get());
                    nowTime.setShowText(StringUtils.EMPTY);
                }
                // 状态变更
                // TODO: 关注状态变更
                if (nowTime.getState() == null) { // 空闲状态
                    nowTime.setState(IDLE);
                }
                if (!nowTime.getState().isDisabledState()) {  // 禁用状态优先级更高
                    if (nowTimeId > timeId) {
                        nowTime.setState(PASSED);
                    } else if (week > 1 || (week == 1 && !nextFlag)) { // 请求未来的周，且未到切换周的时间点
                        nowTime.setState(NOT_OPEN);
                    }
                }
                hourRow.add(new TimePeriodSimpleDTO().convertFrom(nowTime));
                du.tomorrow();  // 下移一天
            }
            timeTable.add(hourRow);
            du.changeWeek(-1);  // 前移一周
        }

        // 返回值构建
        Date weekNumberStart = new Date(optionService.getByProperty(RuntimeProperties.WEEK_START_DATE, Long.class).
                orElse(Long.valueOf(RuntimeProperties.WEEK_START_DATE.defaultValue())));
        TimeTableVO res = new TimeTableVO();
        res.setTimeTable(timeTable);
        res.setTimeTitle(timeTitleList);
        res.setWeek(DateUtils.weekNumberOf(weekNumberStart, du.get()));
        return res;
    }

    private Room checkParamAndAuthority(@NonNull TimeParam param, @NonNull User admin) {
        Assert.notNull(param, "TimeParam 不能为 null");

        // 参数校验
        ArrayList<Long> timeIds = param.getIds();
        if (timeIds.size() == 0) {
            throw new BadRequestException("请至少选择一个时段");
        }
        // 准备基础数据
        int roomId = TimeIdGenerator.getRoomId(timeIds.get(0));
        Room targetRoom = roomService.getById(roomId);

        if (!roomService.roomManagedBy(targetRoom, admin)) {
            throw new ForbiddenException("权限不足，无法操作该活动室");
        }
        return targetRoom;
    }

    private TimeTableVO simpleModifyBy(@NonNull TimeParam param, @NonNull Consumer<List<TimePeriod>> dealer) {
        Assert.notNull(dealer, "处理函数不能为 null");
        // 校验、准备数据
        User admin = SecurityContextHolder.ensureUser();
        Room targetRoom = checkParamAndAuthority(param, admin);
        ArrayList<Long> timeIds = param.getIds();
        // 修改状态
        List<TimePeriod> toModify = listAllByIds(timeIds);
        if (toModify.size() < timeIds.size()) {
            throw new NotFoundException("您不能直接操作空白时段，请检查您的选择");
        }
        dealer.accept(toModify);
        // 返回更新后的表格
        return buildTable(targetRoom, param.getWeek(), admin);
    }

    //******************* Override ******************************************

    @Override
    public TimeTableVO getTimeTable(@NonNull Integer roomId, @NonNull Integer week) {
        Room targetRoom = roomService.getById(roomId);
        User nowUser = SecurityContextHolder.ensureUser();
        return buildTable(targetRoom, week, nowUser);
    }

    @Override
    @CacheLock(prefix = "time_dis_lock", expired = 0, msg = "因为操作冲突，您的请求被取消取消，请重试", argSuffix = "timeId")
    // TODO: 校验，多用户操作时，加锁应该没用
    public TimePeriodSimpleDTO occupyTimePeriod(@NonNull Long timeId) {
        Assert.notNull(timeId, "timeId 不能为 null");
        // 得到目标时段实体
        TimePeriod timeInDB = getByIdOfNullable(timeId);
        if (timeInDB == null) {
            timeInDB = new TimePeriod(timeId);
        }

        // 得到活动室
        Integer roomId = TimeIdGenerator.getRoomId(timeId);
        Room targetRoom = roomService.getById(roomId);

        // 活动室是否开放
        if (!targetRoom.getAvailable()) {
            throw new ForbiddenException("活动室暂未开放");
        }

        // 校验用户权限
        User nowUser = SecurityContextHolder.ensureUser();
        if (!nowUser.getRole().isSystemAdmin() &&
                !roomService.roomAvailableToUser(targetRoom, nowUser)) {
            throw new ForbiddenException("您的权限不足，无法对该活动室进行操作");
        }

        // 校验时段是否合法（可预约，没过期，已开放）
        Date targetDate = TimeIdGenerator.decodeIdToDate(timeId);
        Date now = new Date();
        DateUtils nowData = new DateUtils(now);
        // 新一周开始，当前时间均算作下周一 0 点
        if(nowData.whatDayIsIt() == 7 && nowData.getHour() >= targetRoom.getEndHour()) {
            now = nowData.tomorrow().weekStart().get();
        }
        if (targetDate.before(now)) {
            throw new ForbiddenException("您无法改写历史");
        } else if (DateUtils.weekStartOf(targetDate).after(now)) {
            throw new ForbiddenException("该时段尚未开放预订");
        }

        // 检验是否超出了时长限制
        List<TimePeriod> weekTimePeriods = getWeekTimePeriods(targetRoom, 0);
        int[] statistic = new int[8];  // 统计，0 为周占用，1~7 为周日到周六
        weekTimePeriods.forEach(time -> {
            if (!Objects.equals(time.getUserId(), nowUser.getId())) {
                return;
            }
            statistic[0]++;  // 周统计
            statistic[DateUtils.whatDayIs(time.getStartTime())]++;  // 日统计
        });
        if (statistic[0] >= targetRoom.getWeekLimit() ||
                statistic[DateUtils.whatDayIs(timeInDB.getStartTime())] >= targetRoom.getDayLimit()) {
            throw new ForbiddenException("超出限定时长：周限定: [" + targetRoom.getWeekLimit() + "]，日限定: [" + targetRoom.getDayLimit() + "]");
        }

        // 签到继承
        Long previousTimeId = TimeIdGenerator.previousTimeId(timeId);
        TimePeriod previousTime = getByIdOfNullable(previousTimeId);
        if (previousTime != null && // 前移时段不为 null
                Objects.equals(previousTime.getUserId(), nowUser.getId()) && // 前一时段同为当前用户占用
                previousTime.getSigned() != null && previousTime.getSigned()) { // 前一时段已签到
            timeInDB.setSigned(true); // 本时段自动设为已签到状态
        }

        // 占用时段
        timeInDB.setShowText(nowUser.getRealName());
        timeInDB.setRoomId(roomId);
        timeInDB.setUserId(nowUser.getId());
        timeInDB.setState(OCCUPIED);
        // 存储、应用修改（更新或新建）
        timeInDB = timeRepository.save(timeInDB);
        return new TimePeriodSimpleDTO().convertFrom(timeInDB);
    }

    @Override
    public TimePeriodSimpleDTO cancelTimePeriod(@NonNull Long timeId) {
        Assert.notNull(timeId, "timeId 不能为 null");
        TimePeriod target = getById(timeId);
        User nowUser = SecurityContextHolder.ensureUser();
        // 权限校验：只能解除自己的预约
        if (!Objects.equals(target.getUserId(), nowUser.getId()) && !nowUser.getRole().isSystemAdmin()) {
            throw new ForbiddenException("您没有权限取消该时段的预约");
        }
        // 从数据库中移除
        remove(target);
        return new TimePeriodSimpleDTO().convertFrom(target);
    }

    @Override
    public TimeTableVO blockBy(@NonNull TimeParam param) {
        // 校验、准备数据
        User admin = SecurityContextHolder.ensureUser();
        Room targetRoom = checkParamAndAuthority(param, admin);
        // 整理数据
        List<TimePeriod> toBlock = listAllByIds(param.getIds());
        Map<Long, TimePeriod> toBlockMap = ServiceUtils.convertToMap(toBlock, TimePeriod::getId);
        // 构建需要变更的实体集合
        LinkedList<TimePeriod> toSave = new LinkedList<>();
        param.getIds().forEach(timeId -> {
            TimePeriod targetTime = toBlockMap.get(timeId);
            if (targetTime == null) {  // 该时段为空白时段
                targetTime = new TimePeriod(timeId);
            }
            targetTime.setShowText("");
            targetTime.setUserId(null);
            if (!param.getColorState().isDisabledState()) {
                throw new BadRequestException("颜色不存在");
            }
            targetTime.setState(param.getColorState());
            toSave.add(targetTime);
        });
        // 存储
        updateInBatch(toSave);
        // 返回更新后的表格
        return buildTable(targetRoom, param.getWeek(), admin);
    }

    @Override
    public TimeTableVO clearBy(@NonNull TimeParam param) {
        return simpleModifyBy(param, this::removeAll);
    }

    @Override
    public TimeTableVO changeTextBy(@NonNull TimeParam param) {
        return simpleModifyBy(param, toModify -> {
            toModify.forEach(time -> time.setShowText(param.getShowText()));
            updateInBatch(toModify);
        });
    }
}
