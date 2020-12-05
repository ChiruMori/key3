package work.cxlm.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;
import work.cxlm.cache.AbstractStringCacheStore;
import work.cxlm.exception.DataConflictException;
import work.cxlm.exception.ForbiddenException;
import work.cxlm.exception.NotFoundException;
import work.cxlm.model.dto.LocationDTO;
import work.cxlm.model.dto.RoomDTO;
import work.cxlm.model.entity.Belong;
import work.cxlm.model.entity.Club;
import work.cxlm.model.entity.Room;
import work.cxlm.model.entity.User;
import work.cxlm.model.entity.id.BelongId;
import work.cxlm.model.params.RoomParam;
import work.cxlm.model.support.CreateCheck;
import work.cxlm.model.support.QfzsConst;
import work.cxlm.model.support.UpdateCheck;
import work.cxlm.repository.RoomRepository;
import work.cxlm.security.context.SecurityContextHolder;
import work.cxlm.service.BelongService;
import work.cxlm.service.ClubService;
import work.cxlm.service.RoomService;
import work.cxlm.service.UserService;
import work.cxlm.service.base.AbstractCrudService;
import work.cxlm.utils.ServiceUtils;
import work.cxlm.utils.ValidationUtils;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * @author Chiru
 * program myfont
 * @author beizi
 * @author Chiru
 * create 2020-11-23 20:52
 * application :
 * Version 1.0
 **/
@Service
@Slf4j
public class RoomServiceImpl extends AbstractCrudService<Room, Integer> implements RoomService {

    // ------------------ Autowired ---------------------

    private final RoomRepository roomRepository;
    private final AbstractStringCacheStore cacheStore;

    private ClubService clubService;
    private UserService userService;
    private BelongService belongService;


    protected RoomServiceImpl(RoomRepository roomRepository,
                              AbstractStringCacheStore cacheStore) {
        super(roomRepository);
        this.roomRepository = roomRepository;
        this.cacheStore = cacheStore;
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
            return new RoomDTO().convertFrom(oldRoom);
        }
        // 存储活动室信息
        Room newRoom = param.convertTo();
        newRoom = create(newRoom);
        // 存储归属关系
        Belong newBelong = new Belong(targetClub.getId(), newRoom.getId());
        belongService.create(newBelong);
        return new RoomDTO().convertFrom(newRoom);
    }

    /**
     * 确保权限足以对 Room 进行增删改
     *
     * @param clubId Room 所属的社团
     */
    private void ensureAuthority(Integer clubId) {
        // 验证权限
        User admin = SecurityContextHolder.ensureUser();
        Club targetClub = clubService.getById(clubId);
        if (!userService.managerOfClub(admin, targetClub)) {
            throw new ForbiddenException("权限不足，您无法管理该社团");
        }
    }

    @Override
    @NonNull
    public RoomDTO updateRoomBy(@NonNull RoomParam param) {
        Assert.notNull(param, "RoomParam 不能为 null");
        ensureAuthority(param.getClubId());
        // 更新活动室信息
        Room targetRoom = getById(param.getId());
        param.update(targetRoom);
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
        ensureAuthority(clubId);

        BelongId bid = new BelongId(clubId, roomId);
        if (!belongService.existsById(bid)) {
            throw new NotFoundException("指定的活动室并不属于当前社团");
        }
        // 如果该活动室只归属于一个社团
        boolean onlyOwner = belongService.listRoomClubs(roomId).size() == 1;
        belongService.removeById(bid);  // 删除归属关系
        if (onlyOwner) {  // 删除活动室
            return new RoomDTO().convertFrom(removeById(roomId));
        }
        // 还有其他社团拥有该活动室时
        return new RoomDTO().convertFrom(getById(clubId));
    }

    @Override
    @NonNull
    @SuppressWarnings("rawtypes, unchecked")
    public List<LocationDTO> getLocations(@NonNull Integer clubId) {
        Optional<List> locations = cacheStore.getAny(QfzsConst.LOCATION_KEY, List.class);
        if (locations.isEmpty()) {
            return Collections.emptyList();
        }
        return ServiceUtils.convertList(locations.get(), o -> (LocationDTO) o);
    }
}
