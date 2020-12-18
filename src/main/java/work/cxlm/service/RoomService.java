package work.cxlm.service;

import org.springframework.lang.NonNull;
import org.springframework.transaction.annotation.Transactional;
import work.cxlm.model.dto.LocationDTO;
import work.cxlm.model.dto.RoomDTO;
import work.cxlm.model.entity.Room;
import work.cxlm.model.entity.User;
import work.cxlm.model.params.RoomParam;
import work.cxlm.service.base.CrudService;

import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * @author beizi
 * @author Chiru
 * create: 2020-11-23 15:52
 **/
public interface RoomService extends CrudService<Room, Integer> {

    @NonNull
    Optional<Room> findByRoomId(@NonNull Integer roomId);

    /**
     * 通过表单创建社团活动室的归属关系
     */
    @Transactional
    @NonNull
    RoomDTO newRoomBy(@NonNull RoomParam param);

    /**
     * 通过表单对活动室基本信息进行更新
     */
    @NonNull
    RoomDTO updateRoomBy(@NonNull RoomParam param);

    @NonNull
    Map<Integer, Room> getAllRoomMap();

    /**
     * 获取某社团的全部活动室
     */
    @NonNull
    List<RoomDTO> listClubRooms(@NonNull Integer clubId);

    /**
     * 删除指定的活动室的归属关系，如果活动室只属于一个社团，同时删除活动室
     */
    @NonNull
    @Transactional
    RoomDTO deleteRoom(@NonNull Integer clubId, @NonNull Integer roomId);

    /**
     * 从缓存中查找管理员创建的所有位置信息
     */
    @NonNull
    List<LocationDTO> getLocations(@NonNull Integer clubId);

    /**
     * 通过 ID 查找某活动室的详细信息
     */
    @NonNull
    RoomDTO getOne(@NonNull Integer roomId);

    /**
     * 校验用户是否可以使用该活动室
     */
    boolean roomAvailableToUser(@NonNull Room room, @NonNull User user);

    /**
     * 判定活动室是否归某用户管理
     */
    boolean roomManagedBy(@NonNull Room targetRoom, @NonNull User admin);

    /**
     * 删除活动室及活动室的预约，不检查权限
     */
    void deleteRoomWithoutAuthorityCheck(Integer roomId);
}
