package work.cxlm.service;

import org.springframework.lang.NonNull;
import org.springframework.transaction.annotation.Transactional;
import work.cxlm.model.dto.ClubDTO;
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

    /**
     * 通过 id 查找
     *
     * @param roomId 指定活动室的 id
     * @return Optional 包装的 Room 实例
     */
    @NonNull
    Optional<Room> findByRoomId(@NonNull Integer roomId);

    /**
     * 通过表单创建社团活动室的归属关系
     *
     * @param param 管理后台填写的 Room 表单
     * @return 新建的活动室 DTO 实例
     */
    @Transactional(rollbackFor = Exception.class)
    @NonNull
    RoomDTO newRoomBy(@NonNull RoomParam param);

    /**
     * 通过表单对活动室基本信息进行更新
     *
     * @param param 管理后台填写的 Room 表单
     * @return 更新后的活动室 DTO 实例
     */
    @NonNull
    RoomDTO updateRoomBy(@NonNull RoomParam param);

    /**
     * 获取 id -> Room 的映射 Map
     *
     * @return Map，键为 id，值为 Room 实例
     */
    @NonNull
    Map<Integer, Room> getAllRoomMap();

    /**
     * 获取某社团的全部活动室
     *
     * @param clubId 目标社团 id
     * @return 社团的全部活动室列表
     */
    @NonNull
    List<RoomDTO> listClubRooms(@NonNull Integer clubId);

    /**
     * 删除指定的活动室的归属关系，如果活动室只属于一个社团，同时删除活动室
     *
     * @param clubId 目标社团 id
     * @param roomId 目标活动室 id
     * @return 被删除的活动室 DTO 对象
     */
    @NonNull
    @Transactional(rollbackFor = Exception.class)
    RoomDTO deleteRoom(@NonNull Integer clubId, @NonNull Integer roomId);

    /**
     * 从缓存中查找管理员创建的所有位置信息
     *
     * @param clubId 目标社团 id
     * @return 可用的位置 DTO 列表
     */
    @NonNull
    List<LocationDTO> getLocations(@NonNull Integer clubId);

    /**
     * 通过 ID 查找某活动室的详细信息
     *
     * @param roomId 目标活动室 id
     * @return 目标活动室 DTO 实例
     */
    @NonNull
    RoomDTO getOne(@NonNull Integer roomId);

    /**
     * 校验用户是否可以使用该活动室
     *
     * @param room 指定的活动室实例
     * @param user 指定的用户实例
     * @return 是否可以使用该活动室
     */
    boolean roomAvailableToUser(@NonNull Room room, @NonNull User user);

    /**
     * 判定活动室是否归某用户管理
     *
     * @param targetRoom 指定的活动室实例
     * @param admin      断言的管理员
     * @return 用户是否有权限管理该活动室
     */
    boolean roomManagedBy(@NonNull Room targetRoom, @NonNull User admin);

    /**
     * 删除活动室及活动室的预约，不检查权限
     *
     * @param roomId 指定的活动室 id
     */
    void deleteRoomWithoutAuthorityCheck(Integer roomId);

    /**
     * 获取某活动室的全部社团
     *
     * @param roomId 活动室 ID
     * @return 社团 DTO list
     */
    List<ClubDTO> getRoomClubs(Integer roomId);
}
