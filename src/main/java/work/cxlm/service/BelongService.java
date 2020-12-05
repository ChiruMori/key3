package work.cxlm.service;

import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import work.cxlm.model.entity.Belong;
import work.cxlm.model.entity.Club;
import work.cxlm.model.entity.Room;
import work.cxlm.model.entity.id.BelongId;
import work.cxlm.service.base.CrudService;

import java.util.List;

/**
 * @program: myfont
 * @author: beizi
 * @author Chiru
 * @create: 2020-11-23 15:15
 * @application :
 * @Version 1.0
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
    @Nullable
    List<Room> listClubRooms(@NonNull Integer clubId);

}
