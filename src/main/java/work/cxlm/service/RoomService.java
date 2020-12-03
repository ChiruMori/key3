package work.cxlm.service;

import lombok.NonNull;
import work.cxlm.model.entity.Room;
import work.cxlm.model.entity.User;
import work.cxlm.service.base.CrudService;

import java.util.Optional;

/**
 * @program: myfont
 * @author: beizi
 * @create: 2020-11-23 15:52
 * @application :
 * @Version 1.0
 **/
public interface RoomService extends CrudService<Room, Integer> {

    @NonNull
    Optional<Room> findByRoomId(@NonNull Integer roomId);


}
