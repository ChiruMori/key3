package work.cxlm.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.lang.NonNull;
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
     *
     * @param clubId 目标社团 id
     * @return 某社团的全部用户加入关系
     */
    List<Joining> findAllByIdClubId(Integer clubId);

    /**
     * 通过联合主键的 clubId 分页查找所有 Joining 实体
     *
     * @param clubId   目标社团 id
     * @param pageable 分页参数
     * @return 某社团的用户加入关系分页数据集
     */
    Page<Joining> findAllByIdClubId(Integer clubId, Pageable pageable);


    /**
     * 通过联合主键的 userId 查找所有 Joining 实体
     *
     * @param userId 指定用户 id
     * @return 某用户的全部社团加入关系列表
     */
    List<Joining> findAllByIdUserId(Integer userId);

    /**
     * 通过联合主键的 userId 查找所有 Joining 实体
     *
     * @param userId   指定用户 id
     * @param pageable 分页参数
     * @return 某用户的全部社团加入关系分页数据集
     */
    Page<Joining> findAllByIdUserId(Integer userId, Pageable pageable);

    /**
     * 删除用户的社团加入信息
     *
     * @param userId 指定用户 id
     */
    void deleteByIdUserId(Integer userId);

    /**
     * 删除社团的全部加入信息
     *
     * @param clubId 指定社团 id
     */
    void deleteByIdClubId(Integer clubId);

    /**
     * 查询 userId 在指定列表内的全部 Joining，需要全表扫描，建立索引可以避免
     *
     * @param userIds 目标 user id 列表
     * @return userId 在指定列表内的全部 Joining
     */
    List<Joining> findAllByIdUserIdIn(@NonNull List<Integer> userIds);
}
