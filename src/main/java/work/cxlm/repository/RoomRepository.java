package work.cxlm.repository;

import org.springframework.lang.NonNull;
import work.cxlm.model.entity.Room;
import work.cxlm.model.entity.User;

import java.util.Optional;

/**
 * @program: myfont
 * @author: beizi
 * @create: 2020-11-21 15:17
 * @application :
 * @Version 1.0
 **/
public interface RoomRepository extends BaseRepository<Room,Integer>  {


    @NonNull
    Optional<Room> findById(@NonNull Integer roomId);


}
