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
public interface TimeRepository extends BaseRepository<TimePeriod, Long> {

    @NonNull
    Optional<TimePeriod> findById(@NonNull Long timeId);

    List<TimePeriod> findAllByRoomId(@NonNull Integer roomId);

    List<TimePeriod> findAllByRoomIdAndIdBetween(@NonNull Integer roomId, @NonNull Long minId, @NonNull Long maxId);

    void deleteByUserId(Integer userId);
}
