package work.cxlm.repository;

import work.cxlm.model.entity.Announcement;

import java.util.Collection;
import java.util.List;

/**
 * created 2020/12/15 20:59
 *
 * @author Chiru
 */
public interface AnnouncementRepository extends BaseRepository<Announcement, Integer> {

    List<Announcement> findAllByClubIdIn(Collection<Integer> clubIds);
    List<Announcement> findAllByClubId(Integer clubId);

    void deleteByClubId(Integer clubId);
}
