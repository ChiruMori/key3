package work.cxlm.controller.admin.api;

import io.swagger.annotations.ApiOperation;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import work.cxlm.model.dto.LogDTO;
import work.cxlm.service.LogService;

import java.util.List;

/**
 * 管理员系统日志管理模块
 * created 2020/11/30 10:28
 *
 * @author Chiru
 */
@RestController
@RequestMapping("/key3/admin/api/")
public class LogController {

    private final LogService logService;

    public LogController(LogService logService) {
        this.logService = logService;
    }

    @GetMapping("logs/{clubId}")
    @ApiOperation("管理员获取所有系统日志")
    public List<LogDTO> getAllLogs(@PathVariable Integer clubId) {
        return logService.listAllByClubId(clubId);
    }
}
