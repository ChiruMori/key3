package work.cxlm.controller.mini.api;

import io.swagger.annotations.ApiOperation;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import work.cxlm.model.dto.BillDTO;
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
@RequestMapping("/key3/bill/api/")
public class BillController {

    private final BillService billService;

    public BillController(BillService billService) {
        this.billService = billService;
    }

    @ApiOperation("分页获取指定社团的收支详情")
    @GetMapping("club/{clubId:\\d+}")
    public Page<BillDTO> getClubBills(@PathVariable("clubId") Integer clubId,
                                      @PageableDefault(sort = "createTime", direction = Sort.Direction.DESC) Pageable pageable) {
        return billService.pageClubBills(clubId, pageable);
    }
}
