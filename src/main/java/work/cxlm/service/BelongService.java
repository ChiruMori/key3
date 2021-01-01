package work.cxlm.service;

import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import org.springframework.transaction.annotation.Transactional;
import work.cxlm.model.entity.Belong;
import work.cxlm.model.entity.Club;
import work.cxlm.model.entity.Room;
import work.cxlm.model.entity.id.BelongId;
import work.cxlm.service.base.CrudService;

import java.util.List;

/**
 * @author beizi
 * @author Chiru
 * create 2020-11-23 15:15
 **/
public interface BelongService extends CrudService<Belong, BelongId> {

    /**
     * 查询活动室归属的全部社团
     *
     * @param roomId 社团 ID
     * @return 活动室归属的全部社团列表
     */
    @NonNull
    List<Club> listRoomClubs(@NonNull Integer roomId);


    /**
     * 查看社团拥有的全部活动室列表
     *
     * @param clubId 指定的社团 id
     * @return 社团拥有的全部活动室列表
     */
    @NonNull
    List<Room> listClubRooms(@NonNull Integer clubId);

    /**
     * 删除归属于社团的活动室
     *
     * @param clubId 活动室 id
     */
    @Transactional(rollbackFor = Exception.class)
    void deleteClubRooms(@NonNull Integer clubId);
}
