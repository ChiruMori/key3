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

    /**
     * 查询某个活动室某个时间段内全部时段
     *
     * @param roomId 活动室 ID
     * @param minId  开始时间的 ID
     * @param maxId  结束时间的 ID
     * @return 活动室的全部时段列表
     */
    List<TimePeriod> findAllByRoomIdAndIdBetween(@NonNull Integer roomId, @NonNull Long minId, @NonNull Long maxId);

    /**
     * 删除某用户预约的全部时段
     *
     * @param userId 用户 ID
     */
    void deleteByUserId(Integer userId);
}
