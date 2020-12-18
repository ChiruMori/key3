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
     * 根据roomId查到属于哪些社团
     */
    @NonNull
    List<Club> listRoomClubs(@NonNull Integer roomId);


    /**
     * 根据roomId查到属于哪些社团
     */
    @NonNull
    List<Room> listClubRooms(@NonNull Integer clubId);

    /**
     * 删除归属于社团的活动室
     */
    @Transactional
    void deleteClubRooms(@NonNull Integer clubId);
}
