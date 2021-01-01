package work.cxlm.repository;

import cn.hutool.core.collection.CollUtil;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import work.cxlm.model.entity.Bill;

import java.util.List;

/**
 * created 2020/11/26 14:44
 *
 * @author Chiru
 */
public interface BillRepository extends BaseRepository<Bill, Integer> {

    /**
     * 分页查询社团的账单
     *
     * @param clubId         社团 ID
     * @param latestPageable 分页请求
     * @return 社团账单的分页数据集
     */
    Page<Bill> findAllByClubId(Integer clubId, Pageable latestPageable);

    /**
     * 查询社团的全部账单（前端分页）
     *
     * @param clubId 社团 ID
     * @return 社团全部账单的列表
     */
    List<Bill> findAllByClubId(Integer clubId);

    /**
     * 删除社团全部财务信息
     *
     * @param clubId 目标社团 id
     */
    void deleteByClubId(Integer clubId);
}
