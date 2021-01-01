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

    /**
     * 通过联合主键查询拥有roomId的belong实例
     *
     * @param roomId 活动室 id
     * @return 该活动室的全部归属关系列表
     */
    List<Belong> findAllByIdRoomId(@NonNull Integer roomId);

    /**
     * 通过联合主键查询拥有clubId的belong实例
     *
     * @param clubId 社团 ID
     * @return 该社团的全部活动室归属关系
     */
    List<Belong> findAllByIdClubId(@NonNull Integer clubId);

    /**
     * 删除归属于某社团的全部活动室
     *
     * @param clubId 目标社团的 id
     */
    void deleteByIdClubId(Integer clubId);
}
