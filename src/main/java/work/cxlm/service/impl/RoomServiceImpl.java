package work.cxlm.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;
import work.cxlm.cache.AbstractStringCacheStore;
import work.cxlm.event.LogEvent;
import work.cxlm.event.RoomInfoUpdatedEvent;
import work.cxlm.exception.DataConflictException;
import work.cxlm.exception.ForbiddenException;
import work.cxlm.exception.NotFoundException;
import work.cxlm.model.dto.ClubDTO;
import work.cxlm.model.dto.LocationDTO;
import work.cxlm.model.dto.RoomDTO;
import work.cxlm.model.entity.*;
import work.cxlm.model.entity.id.BelongId;
import work.cxlm.model.entity.support.TimeIdGenerator;
import work.cxlm.model.params.LogParam;
import work.cxlm.model.params.RoomParam;
import work.cxlm.model.support.QfzsConst;
import work.cxlm.repository.RoomRepository;
import work.cxlm.security.context.SecurityContextHolder;
import work.cxlm.service.*;
import work.cxlm.service.base.AbstractCrudService;
import work.cxlm.utils.ServiceUtils;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @author beizi
 * @author Chiru
 * create 2020-11-23 20:52
 */
@Service
@Slf4j
public class RoomServiceImpl extends AbstractCrudService<Room, Integer> implements RoomService {

    // ------------------ Autowired ---------------------

    private final RoomRepository roomRepository;
    private final AbstractStringCacheStore cacheStore;
    private final ApplicationEventPublisher eventPublisher;

    private ClubService clubService;
    private UserService userService;
    private BelongService belongService;
    private JoiningService joiningService;
    private TimeService timeService;

    protected RoomServiceImpl(RoomRepository roomRepository,
                              AbstractStringCacheStore cacheStore,
                              ApplicationEventPublisher eventPublisher) {
        super(roomRepository);
        this.roomRepository = roomRepository;
        this.cacheStore = cacheStore;
        this.eventPublisher = eventPublisher;
    }

    @Autowired
    public void setClubService(ClubService clubService) {
        this.clubService = clubService;
    }

    @Autowired
    public void setUserService(UserService userService) {
        this.userService = userService;
    }

    @Autowired
    public void setBelongService(BelongService belongService) {
        this.belongService = belongService;
    }

    @Autowired
    public void setJoiningService(JoiningService joiningService) {
        this.joiningService = joiningService;
    }

    @Autowired
    public void setTimeService(TimeService timeService) {
        this.timeService = timeService;
    }

    // ----------- Override --------------------------------------

    @Override
    public @NonNull
    Optional<Room> findByRoomId(@NonNull Integer roomId) {
        return roomRepository.findById(roomId);
    }

    @Override
    @Transactional
    @NonNull
    public RoomDTO newRoomBy(@NonNull RoomParam param) {
        Assert.notNull(param, "RoomParam 不能为 null");

        User admin = SecurityContextHolder.ensureUser();
        Club targetClub = clubService.getById(param.getClubId());
        if (!userService.managerOfClub(admin, targetClub)) {
            throw new ForbiddenException("权限不足，您无法管理该社团");
        }
        // 多个社团共用活动室的情况
        Integer oldRoomId = param.getId();
        if (oldRoomId != null) {
            BelongId bid = new BelongId(targetClub.getId(), oldRoomId);
            if (belongService.existsById(bid)) {  // 已经存在该归属关系
                throw new DataConflictException("活动室已经属于该社团，请核对后重试");
            }
            // 存储归属关系
            belongService.create(new Belong(bid));
            // 更新活动室信息
            Room oldRoom = getById(oldRoomId);
            param.update(oldRoom);
            oldRoom = update(oldRoom);
            // 通知变更
            eventPublisher.publishEvent(new RoomInfoUpdatedEvent(this));
            eventPublisher.publishEvent(new LogEvent(this, new LogParam(admin.getId(), targetClub.getId(),
                    "更新了社团活动室" + oldRoom.getName())));
            return new RoomDTO().convertFrom(oldRoom);
        }
        // 存储活动室信息
        Room newRoom = param.convertTo();
        newRoom = create(newRoom);
        // 存储归属关系
        Belong newBelong = new Belong(targetClub.getId(), newRoom.getId());
        belongService.create(newBelong);
        // 通知变更
        eventPublisher.publishEvent(new RoomInfoUpdatedEvent(this));
        eventPublisher.publishEvent(new LogEvent(this, new LogParam(admin.getId(), targetClub.getId(),
                "增加了社团活动室：" + newRoom.getName())));
        return new RoomDTO().convertFrom(newRoom);
    }

    @Override
    @NonNull
    public RoomDTO updateRoomBy(@NonNull RoomParam param) {
        Assert.notNull(param, "RoomParam 不能为 null");
        // 验证权限
        User admin = SecurityContextHolder.ensureUser();
        Club targetClub = clubService.getById(param.getClubId());
        if (!userService.managerOfClub(admin, targetClub)) {
            throw new ForbiddenException("权限不足，您无法管理该社团");
        }
        // 更新活动室信息
        Room targetRoom = getById(param.getId());
        param.update(targetRoom);
        timeService.removeOutTime(targetRoom);  // 移除超出新时间区间的预约
        // 通知变更
        eventPublisher.publishEvent(new RoomInfoUpdatedEvent(this));
        eventPublisher.publishEvent(new LogEvent(this, new LogParam(admin.getId(), targetClub.getId(),
                "修改了社团活动室：" + targetRoom.getName())));
        return new RoomDTO().convertFrom(update(targetRoom));
    }

    @Override
    @NonNull
    public Map<Integer, Room> getAllRoomMap() {
        List<Room> allRoom = roomRepository.findAll();
        return ServiceUtils.convertToMap(allRoom, Room::getId);
    }

    @Override
    @NonNull
    public List<RoomDTO> listClubRooms(@NonNull Integer clubId) {
        Assert.notNull(clubId, "社团 ID 不能为 null");
        return ServiceUtils.convertList(belongService.listClubRooms(clubId), r -> new RoomDTO().convertFrom(r));
    }

    @Override
    @Transactional
    @NonNull
    public RoomDTO deleteRoom(@NonNull Integer clubId, @NonNull Integer roomId) {
        Assert.notNull(clubId, "clubId 不能为 null");
        Assert.notNull(roomId, "roomId 不能为 null");
        // 验证权限
        User admin = SecurityContextHolder.ensureUser();
        Club targetClub = clubService.getById(clubId);
        if (!userService.managerOfClub(admin, targetClub)) {
            throw new ForbiddenException("权限不足，您无法管理该社团");
        }

        BelongId bid = new BelongId(clubId, roomId);
        if (!belongService.existsById(bid)) {
            throw new NotFoundException("指定的活动室并不属于当前社团");
        }
        // 如果该活动室只归属于一个社团
        boolean onlyOwner = belongService.listRoomClubs(roomId).size() == 1;
        belongService.removeById(bid);  // 删除归属关系
        // 通知变更
        eventPublisher.publishEvent(new RoomInfoUpdatedEvent(this));
        RoomDTO res;
        if (onlyOwner) {  // 删除活动室，并删除预约历史记录
            deleteRoomTimePeriods(roomId);  // 删除该活动室的预约
            res = new RoomDTO().convertFrom(removeById(roomId));
        } else {
            // 还有其他社团拥有该活动室时
            res = new RoomDTO().convertFrom(getById(clubId));
        }
        eventPublisher.publishEvent(new LogEvent(this, new LogParam(admin.getId(), targetClub.getId(),
                "修改了社团活动室：" + res.getName())));
        return res;
    }

    @Override
    @NonNull
    @SuppressWarnings("rawtypes, unchecked")
    public List<LocationDTO> getLocations(@NonNull Integer clubId) {
        Optional<List> locations = cacheStore.getAny(QfzsConst.LOCATION_KEY, List.class);
        return (List<LocationDTO>) locations.  // 这里的转化不能删除，否则在 JDK8 环境将报错
                map(list -> ServiceUtils.convertList(list, o -> (LocationDTO) o)).
                orElse(Collections.emptyList());
    }

    @Override
    @NonNull
    public RoomDTO getOne(@NonNull Integer roomId) {
        Assert.notNull(roomId, "RoomId 不能为 null");
        return new RoomDTO().convertFrom(getById(roomId));
    }

    @Override
    public boolean roomAvailableToUser(@NonNull Room room, @NonNull User user) {
        return hasRelationBetweenRoomAndUser(room, user, false);
    }

    @Override
    public boolean roomManagedBy(@NonNull Room room, @NonNull User admin) {
        return hasRelationBetweenRoomAndUser(room, admin, true);
    }

    @Override
    public void deleteRoomWithoutAuthorityCheck(Integer roomId) {
        Assert.notNull(roomId, "roomId 不能为 null");

        deleteRoomTimePeriods(roomId);
        removeById(roomId);
    }

    @Override
    public List<ClubDTO> getRoomClubs(Integer roomId) {
        return ServiceUtils.convertList(belongService.listRoomClubs(roomId), club -> new ClubDTO().convertFrom(club));
    }

    @Override
    public List<ClubDTO> getRoomClubs(Integer roomId) {
        return ServiceUtils.convertList(belongService.listRoomClubs(roomId), club -> new ClubDTO().convertFrom(club));
    }

    // ***************** Private *********************

    private boolean hasRelationBetweenRoomAndUser(@NonNull Room room, @NonNull User user, boolean mustAdmin) {
        Assert.notNull(room, "room 不能为 null");
        Assert.notNull(user, "user 不能为 null");
        // 系统管理员无视权限
        if (user.getRole().isSystemAdmin()) {
            return true;
        }
        // 活动室所属的所有社团
        List<Club> roomClubs = belongService.listRoomClubs(room.getId());
        List<Joining> userJoining = joiningService.listAllJoiningByUserId(user.getId());
        // 获得用户加入的全部社团
        Set<Integer> userClubs = userJoining.stream().
                filter(joining -> !mustAdmin || joining.getAdmin()).  // 在需要管理员权限时进行过滤
                map(joining -> joining.getId().getClubId()).
                collect(Collectors.toSet());
        // 用户加入的社团与活动室所属的社团有交集，则认为用户可以对该活动室进行操作
        return roomClubs.stream().anyMatch(roomClub -> userClubs.contains(roomClub.getId()));
    }

    private void deleteRoomTimePeriods(@NonNull Integer roomId) {
        List<TimePeriod> timePeriods = timeService.listAll();
        timePeriods = timePeriods.stream().
                filter(time -> Objects.equals(TimeIdGenerator.getRoomId(time.getId()), roomId)).
                collect(Collectors.toList());
        timeService.removeAll(timePeriods);
    }
}
