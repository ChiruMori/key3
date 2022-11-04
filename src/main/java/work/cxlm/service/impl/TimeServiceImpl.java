package work.cxlm.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;
import work.cxlm.event.LogEvent;
import work.cxlm.exception.BadRequestException;
import work.cxlm.exception.ForbiddenException;
import work.cxlm.exception.FrequentAccessException;
import work.cxlm.exception.NotFoundException;
import work.cxlm.lock.CacheLock;
import work.cxlm.model.dto.TimePeriodSimpleDTO;
import work.cxlm.model.entity.*;
import work.cxlm.model.entity.support.TimeIdGenerator;
import work.cxlm.model.enums.NoticeType;
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
    private BelongService belongService;
    private NoticeService noticeService;
    private ApplicationEventPublisher eventPublisher;

    private final TimeRepository timeRepository;
    private final OptionService optionService;

    private static final int WEEKEND = 7;

    @Autowired
    public void setBelongService(BelongService belongService) {
        this.belongService = belongService;
    }

    @Autowired
    public void setRoomService(RoomService roomService) {
        this.roomService = roomService;
    }

    @Autowired
    public void setNoticeService(NoticeService noticeService) {
        this.noticeService = noticeService;
    }

    @Autowired
    public void setEventPublisher(ApplicationEventPublisher eventPublisher) {
        this.eventPublisher = eventPublisher;
    }

    public TimeServiceImpl(TimeRepository timeRepository,
                           OptionService optionService) {
        super(timeRepository);
        this.timeRepository = timeRepository;
        this.optionService = optionService;
    }

    // ***************** Private ***********************************

    /**
     * 获得指定周、活动室的全部时间段
     */
    private List<TimePeriod> getWeekTimePeriods(@NonNull Room room, @NonNull Integer week) {
        Assert.notNull(room, "room 不能为 null");
        Assert.notNull(week, "请求的周次不能为 null");

        Integer roomId = room.getId();
        DateUtils du = new DateUtils(new Date());
        // 移动到新的一周
        if (du.whatDayIsIt() == WEEKEND && du.getHour() >= room.getEndHour()) {
            du.tomorrow();
        }
        du.weekStart().changeWeek(week + 1);
        Long endTimeId = TimeIdGenerator.encodeId(du, roomId);
        Long startTimeId = TimeIdGenerator.encodeId(du.changeWeek(-1), roomId);
        return timeRepository.findAllByRoomIdAndIdBetween(roomId, startTimeId, endTimeId);
    }

    /**
     * 构建指定活动室、周次、用户的预约表格
     */
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
        if (du.whatDayIsIt() == WEEKEND && du.getHour() >= targetRoom.getEndHour()) {
            week += 1;
            nextFlag = true;
        }
        // 获得当前时间点的 ID
        long nowTimeId = TimeIdGenerator.encodeId(du.lastHour(), 0);
        du.nextHour();
        // 移动到指定周周一
        du.changeWeek(week).weekStart();
        for (int i = startHour; i < endHour; i++) {
            // 生成行标题
            du.setHour(i + 1);
            String endTimeTitle = du.generateTitle();
            String startTimeTitle = du.setHour(i).generateTitle();
            timeTitleList.add(String.format("%s %2s", startTimeTitle, endTimeTitle));

            // 行数据整理
            LinkedList<TimePeriodSimpleDTO> hourRow = new LinkedList<>();
            for (int j = 0; j < WEEKEND; j++) {
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
                // 废弃: 关注状态变更

                // 空闲状态
                if (nowTime.getState() == null) {
                    nowTime.setState(targetRoom.getAvailable() ? IDLE : DISABLED_RED);
                }
                // 禁用状态优先级更高
                if (!nowTime.getState().isDisabledState()) {
                    boolean nextWeekOrNotOpen = week > 1 || (week == 1 && !nextFlag);
                    if (nowTimeId > timeId) {
                        nowTime.setState(PASSED);
                        // 请求未来的周，且未到切换周的时间点
                    } else if (nextWeekOrNotOpen) {
                        nowTime.setState(NOT_OPEN);
                    }
                }
                hourRow.add(new TimePeriodSimpleDTO().convertFrom(nowTime));
                // 下移一天
                du.tomorrow();
            }
            timeTable.add(hourRow);
            // 前移一周
            du.changeWeek(-1);
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
    // 针对时段 ID 加锁，非阻塞锁，重复获取锁时失败，方法结束后自动解锁，防止多用户同时操作造成的数据不一致
    @CacheLock(prefix = "time_dis_lock", msg = "因为操作冲突，您的请求被取消，请重试", argSuffix = "timeId")
    public TimePeriodSimpleDTO occupyTimePeriod(@NonNull Long timeId, @Nullable Integer clubId) {
        Assert.notNull(timeId, "timeId 不能为 null");

        // 得到当前用户
        User nowUser = SecurityContextHolder.ensureUser();
        // 得到目标时段实体
        TimePeriod timeInDb = getByIdOfNullable(timeId);
        if (timeInDb == null) {
            timeInDb = new TimePeriod(timeId);
        } else {
            if (timeInDb.getUserId().equals(nowUser.getId())) {
                throw new FrequentAccessException("这个时段已经是您的了");
            }
            throw new ForbiddenException("手速慢了，该时段被别人抢去了");
        }

        // 得到活动室
        Integer roomId = TimeIdGenerator.getRoomId(timeId);
        Room targetRoom = roomService.getById(roomId);

        // 活动室是否开放
        if (!targetRoom.getAvailable()) {
            throw new ForbiddenException("活动室暂未开放");
        }

        // 校验用户权限
        if (!nowUser.getRole().isSystemAdmin() &&
                !roomService.roomAvailableToUser(targetRoom, nowUser)) {
            throw new ForbiddenException("您的权限不足，无法对该活动室进行操作");
        }

        // 校验时段是否合法（可预约，没过期，已开放）
        Date targetDate = TimeIdGenerator.decodeIdToDate(timeId);
        DateUtils nowDate = new DateUtils();
        Date lastHour = new DateUtils().lastHour().get();
        // 新一周开始，当前时间均算作下周一 0 点
        if (nowDate.whatDayIsIt() == WEEKEND && nowDate.getHour() >= targetRoom.getEndHour()) {
            nowDate.tomorrow().weekStart().get();
        }
        if (targetDate.before(lastHour)) {
            throw new ForbiddenException("您无法改写历史");
        } else if (DateUtils.weekStartOf(targetDate).after(nowDate.get())) {
            throw new ForbiddenException("该时段尚未开放预订");
        }

        // 检验是否超出了时长限制
        List<TimePeriod> weekTimePeriods = getWeekTimePeriods(targetRoom, 0);
        Date now = new DateUtils().lastHour().get();
        // 统计，0 为周占用，1~7 为周日到周六
        int[] statistic = new int[8];
        weekTimePeriods.forEach(time -> {
            // 不是自己的时间、已过去的时间不进行统计
            if (!Objects.equals(time.getUserId(), nowUser.getId()) || time.getStartTime().before(now)) {
                return;
            }
            statistic[0]++;  // 周统计
            statistic[DateUtils.whatDayIs(time.getStartTime())]++;  // 日统计
        });
        if (statistic[0] >= targetRoom.getWeekLimit() ||
                statistic[DateUtils.whatDayIs(timeInDb.getStartTime())] >= targetRoom.getDayLimit()) {
            throw new ForbiddenException("超出限定时长：周限定: [" + targetRoom.getWeekLimit() + "]，日限定: [" + targetRoom.getDayLimit() + "]");
        }

        // 签到继承
        Long previousTimeId = TimeIdGenerator.previousTimeId(timeId);
        TimePeriod previousTime = getByIdOfNullable(previousTimeId);
        // 前移时段不为 null
        if (previousTime != null &&
                // 前一时段同为当前用户占用
                Objects.equals(previousTime.getUserId(), nowUser.getId()) &&
                // 前一时段已签到
                previousTime.getSigned() != null && previousTime.getSigned()) {
            // 本时段自动设为已签到状态
            timeInDb.setSigned(true);
        }

        // 占用时段
        timeInDb.setShowText(nowUser.getRealName());
        timeInDb.setRoomId(roomId);
        timeInDb.setUserId(nowUser.getId());
        timeInDb.setState(OCCUPIED);
        // 存储、应用修改（更新或新建）
        timeInDb = timeRepository.save(timeInDb);
        String logContent = String.format("用户 [%s] 预定了活动室 [%s] 的时段：%s", nowUser.getRealName(), targetRoom.getName(),
                new DateUtils(TimeIdGenerator.decodeIdToDate(timeId)).getFormattedTime());
        log.info(logContent);
        // 生成社团级别日志
        eventPublisher.publishEvent(new LogEvent(timeId, nowUser.getId(), clubId, logContent));
        return new TimePeriodSimpleDTO().convertFrom(timeInDb);
    }

    @Override
    public TimePeriodSimpleDTO cancelTimePeriod(@NonNull Long timeId, @Nullable Integer clubId) {
        Assert.notNull(timeId, "timeId 不能为 null");
        TimePeriod target = getById(timeId);
        User nowUser = SecurityContextHolder.ensureUser();
        // 权限校验：只能解除自己的预约
        if (!Objects.equals(target.getUserId(), nowUser.getId()) && !nowUser.getRole().isSystemAdmin()) {
            throw new ForbiddenException("您没有权限取消该时段的预约");
        }
        // 从数据库中移除
        remove(target);
        String logContent = String.format("用户 [%s] 取消了预约，时段ID：%s", nowUser.getRealName(), timeId);
        log.info(logContent);
        // 生成社团级别日志
        eventPublisher.publishEvent(new LogEvent(timeId, nowUser.getId(), clubId, logContent));
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
        // 日志
        log.info("管理员禁用了时段，禁用的时段 ID，[{}]", toSave);
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

    @Override
    public void deleteByUserId(@NonNull Integer userId) {
        if (userId == -1) {
            log.error("危险：尝试删除系统用户的预定，已被阻止");
            return;
        }
        timeRepository.deleteByUserId(userId);
    }

    @Override
    public float getWeekUsage(int weekNum, @NonNull Club targetClub) {
        Assert.notNull(targetClub, "指定的社团不能为 null");

        List<Room> rooms = belongService.listClubRooms(targetClub.getId());
        int[] usedAndAll = new int[2];
        // 大概是社团没有活动室的情况
        if (CollectionUtils.isEmpty(rooms)) {
            return 0.0f;
        }
        rooms.forEach(room -> {
            List<TimePeriod> timePeriods = getWeekTimePeriods(room, weekNum);
            usedAndAll[0] += timePeriods.size();
            usedAndAll[1] += (room.getEndHour() - room.getStartHour()) * 7;
        });
        return (float) usedAndAll[0] / (float) usedAndAll[1] * 100;
    }

    @Override
    public void deleteUserFutureTime(@NonNull User targetUser, @NonNull Club targetClub) {
        Assert.notNull(targetUser, "目标用户不能为 null");
        Assert.notNull(targetClub, "目标社团不能为 null");

        List<Room> rooms = belongService.listClubRooms(targetClub.getId());
        List<TimePeriod> toRemove = new LinkedList<>();
        Date now = new DateUtils().lastHour().get();
        rooms.forEach(room -> getWeekTimePeriods(room, 0).forEach(time -> {
            if (TimeIdGenerator.decodeIdToDate(time.getId()).after(now)) {
                toRemove.add(time);
            }
        }));
        removeAll(toRemove);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void removeOutTime(@NonNull Room targetRoom) {
        Assert.notNull(targetRoom, "目标活动室不能为 null");

        User admin = SecurityContextHolder.ensureUser();
        LinkedList<TimePeriod> timeToDelete = new LinkedList<>();
        List<TimePeriod> weekTimePeriods = getWeekTimePeriods(targetRoom, 0);
        List<Notice> notices = new LinkedList<>();
        weekTimePeriods.forEach(timePeriod -> {
            int timePeriodHour = TimeIdGenerator.decodeHourFromId(timePeriod.getId());
            if (timePeriodHour < targetRoom.getStartHour() || timePeriodHour >= targetRoom.getEndHour()) {
                timeToDelete.add(timePeriod);
                if (timePeriod.getUserId() == -1) {
                    return;
                }
                notices.add(new Notice(NoticeType.TIME_DELETE, "因活动室【" + targetRoom.getName() +
                        "】开放时间调整，您预约的时段：【" + new DateUtils(timePeriod.getStartTime()).getFormattedTime() + "】已被取消",
                        admin.getId(), timePeriod.getUserId()));
            }
        });
        removeAll(timeToDelete);
        noticeService.notifyByMailInBatch(notices);
    }

    @Override
    public List<TimePeriod> listAllTimeByIdBetween(@NonNull Long minId, @NonNull Long maxId) {
        Assert.notNull(minId, "minId 不能为 null");
        Assert.notNull(maxId, "maxId 不能为 null");
        return timeRepository.findAllByIdBetween(minId, maxId);
    }
}
