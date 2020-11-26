package work.cxlm.repository;

import cn.hutool.core.collection.CollUtil;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import work.cxlm.model.entity.Bill;

/**
 * created 2020/11/26 14:44
 *
 * @author Chiru
 */
public interface BillRepository extends BaseRepository<Bill, Integer> {

    /**
     * 分页查询社团的账单
     * @param clubId 社团 ID
     * @param latestPageable 分页请求
     */
    Page<Bill> findAllyByClubId(Integer clubId, Pageable latestPageable);
}
