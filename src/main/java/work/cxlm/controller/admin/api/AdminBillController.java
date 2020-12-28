package work.cxlm.controller.admin.api;

import io.swagger.annotations.ApiOperation;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import work.cxlm.model.params.BillParam;
import work.cxlm.model.vo.BillTableVO;
import work.cxlm.model.vo.BillVO;
import work.cxlm.service.BillService;

/**
 * 社团财务管理员接口
 * created 2020/11/29 11:30
 *
 * @author Chiru
 */
@RestController
@RequestMapping("/key3/admin/api/")
public class AdminBillController {

    private final BillService billService;

    public AdminBillController(BillService billService) {
        this.billService = billService;
    }

    @ApiOperation("获取指定社团的所有收支列表")
    @GetMapping("bills/{clubId:\\d+}")
    public BillTableVO getBillTableData(@PathVariable("clubId") Integer clubId) {
        return billService.listClubAllBill(clubId);
    }

    @ApiOperation("新建收支")
    @PostMapping("bills")
    public BillVO newBill(@RequestBody @Validated BillParam param) {
        return billService.saveBillBy(param);
    }

    @ApiOperation("更新收支内容")
    @PutMapping("bills")
    public BillVO updateBill(@RequestBody @Validated BillParam param) {
        return billService.saveBillBy(param);
    }

    @ApiOperation("删除某个收支项")
    @DeleteMapping("bills/{billId:\\d+}")
    public BillVO deleteBillById(@PathVariable("billId") Integer billId) {
        return billService.deleteBill(billId);
    }
}
