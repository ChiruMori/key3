package work.cxlm.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import work.cxlm.model.dto.AnnouncementDTO;
import work.cxlm.model.entity.Announcement;

import java.util.Collection;
import java.util.List;

/**
 * created 2020/12/15 20:59
 *
 * @author Chiru
 */
public interface AnnouncementRepository extends BaseRepository<Announcement, Integer> {

    /**
     * 查询某些社团的公告
     *
     * @param clubIds 要查询的社团 id 集合
     * @return 指定社团所有公告实体的列表
     */
    List<Announcement> findAllByClubIdIn(Collection<Integer> clubIds);

    /**
     * 查询社团的全部公告
     *
     * @param clubId 要查询的社团 id
     * @return 指定社团所有公告实体的列表
     */
    List<Announcement> findAllByClubId(Integer clubId);

    /**
     * 删除社团的全部公告
     *
     * @param clubId 指定社团 id
     */
    void deleteByClubId(Integer clubId);

    /**
     * 分页查询社团的全部公告
     *
     * @param clubId   指定社团 id
     * @param pageable 分页参数
     * @return 公告实体的分页查询结果
     */
    Page<Announcement> findAllByClubId(Integer clubId, Pageable pageable);
}
