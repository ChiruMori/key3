package work.cxlm.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import work.cxlm.model.entity.Joining;
import work.cxlm.model.entity.id.JoiningId;

import java.util.List;

/**
 * created 2020/11/18 12:52
 *
 * @author Chiru
 */
public interface JoiningRepository extends BaseRepository<Joining, JoiningId> {

    /**
     * 通过联合主键的 clubId 查找所有 Joining 实体
     */
    List<Joining> findAllByIdClubId(Integer clubId);
    Page<Joining> findAllByIdClubId(Integer clubId, Pageable pageable);


    /**
     * 通过联合主键的 userId 查找所有 Joining 实体
     */
    List<Joining> findAllByIdUserId(Integer userId);
    Page<Joining> findAllByIdUserId(Integer userId, Pageable pageable);

    /**
     * 删除用户的社团加入信息
     */
    void deleteByIdUserId(Integer userId);

    /**
     * 删除社团的全部加入信息
     */
    void deleteByIdClubId(Integer clubId);
}
