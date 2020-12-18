package work.cxlm.controller.mini.api;

import io.swagger.annotations.ApiOperation;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.*;
import work.cxlm.model.dto.NoticeDTO;
import work.cxlm.model.params.NoticeParam;
import work.cxlm.service.NoticeService;

/**
 * created 2020/12/10 22:49
 *
 * @author Chiru
 */
@RestController
@RequestMapping("/key3/notice/api")
public class NoticeController {

    private final NoticeService noticeService;

    public NoticeController(NoticeService noticeService) {
        this.noticeService = noticeService;
    }

    @GetMapping("/page")
    @ApiOperation("分页获取用户的全部通知")
    public Page<NoticeDTO> pageNoticeBy(@PageableDefault(sort = "createTime", direction = Sort.Direction.DESC) Pageable pageable) {
        return noticeService.pageAllNoticeOfUser(pageable);
    }

    @PostMapping("/note")
    @ApiOperation("给用户留言")
    public void leaveANote(@RequestBody NoticeParam param) {
        noticeService.leaveANoteBy(param);
    }

}
