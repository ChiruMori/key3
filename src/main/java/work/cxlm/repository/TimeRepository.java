package work.cxlm.repository;

import org.springframework.lang.NonNull;
import work.cxlm.model.entity.Room;
import work.cxlm.model.entity.TimePeriod;

import java.util.List;
import java.util.Optional;

/**
 * created 2020/11/16 23:08
 *
 * @author Chiru
 */
public interface TimeRepository extends BaseRepository<TimePeriod, Integer> {

    @NonNull
    Optional<TimePeriod> findById(@NonNull Integer timeId);

    List<TimePeriod> findAllByRoomId(@NonNull Integer roomId);

}
