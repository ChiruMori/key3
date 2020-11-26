package work.cxlm.service;

import org.springframework.data.domain.Page;
import work.cxlm.model.dto.BillDTO;
import work.cxlm.model.entity.Bill;
import work.cxlm.service.base.CrudService;

/**
 * created 2020/11/26 14:38
 *
 * @author Chiru
 */
public interface BillService extends CrudService<Bill, Integer> {

    /**
     * 列出某社团可以查看的最近日志
     * @param top 条目数
     * @param clubId 社团 ID
     * @param showHead 是否显示用户相关信息
     */
    Page<BillDTO> pageClubLatest(int top, Integer clubId, boolean showHead);
}
