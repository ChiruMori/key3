package work.cxlm.service;

import org.springframework.data.domain.Page;
import org.springframework.lang.NonNull;
import org.springframework.transaction.annotation.Transactional;
import work.cxlm.model.dto.BillDTO;
import work.cxlm.model.entity.Bill;
import work.cxlm.model.params.BillParam;
import work.cxlm.model.vo.BillTableVO;
import work.cxlm.model.vo.BillVO;
import work.cxlm.service.base.CrudService;

/**
 * created 2020/11/26 14:38
 *
 * @author Chiru
 */
public interface BillService extends CrudService<Bill, Integer> {

    /**
     * 列出某社团前 top 条收支
     *
     * @param top      条目数
     * @param clubId   社团 ID
     * @param showHead 是否显示用户相关信息
     */
    Page<BillDTO> pageClubLatest(int top, Integer clubId, boolean showHead);

    void removeByClubId(Integer clubId);

    /**
     * 列出某社团全部收支（前端分页）
     *
     * @param clubId 社团 ID
     * @return DataTable 需要的数据
     */
    BillTableVO listClubAllBill(Integer clubId);

    /**
     * 通过表单创建或更新收支实体，param 中有 ID 时判定为更新，否则为新建
     *
     * @param param 表单对象
     * @return 新建的收支 VO
     */
    @Transactional
    BillVO saveBillBy(@NonNull BillParam param);

    /**
     * 通过 ID 删除收支项
     * @param billId 收支 ID
     */
    @Transactional
    BillVO deleteBill(Integer billId);
}
