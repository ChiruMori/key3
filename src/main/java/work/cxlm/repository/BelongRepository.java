package work.cxlm.repository;

import org.springframework.lang.NonNull;
import work.cxlm.model.entity.Belong;
import work.cxlm.model.entity.id.BelongId;

import java.util.List;

/**
 * @author beizi
 * create: 2020-11-23 15:11
 **/
public interface BelongRepository extends BaseRepository<Belong, BelongId> {

    /*
     * 通过联合主键查询拥有roomId的belong实例
     */
    List<Belong> findAllByIdRoomId(@NonNull Integer roomId);

    /*
     * 通过联合主键查询拥有clubId的belong实例
     */
    List<Belong> findAllByIdClubId(@NonNull Integer clubId);

    /**
     * 删除归属于某社团的全部活动室
     */
    void deleteByIdClubId(Integer clubId);
}
