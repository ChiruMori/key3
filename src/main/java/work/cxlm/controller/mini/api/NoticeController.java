package work.cxlm.controller.mini.api;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import work.cxlm.model.dto.NoticeDTO;
import work.cxlm.service.NoticeService;

/**
 * TODO: 新公告发布时
 * TODO: 关注可用时
 * TODO: 新增留言时
 * TODO: 时间表重置时
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

    /**
     * 分页列出某个用户的全部通知
     */
    public Page<NoticeDTO> pageNoticeBy(@PageableDefault(sort = "create_time", direction = Sort.Direction.DESC) Pageable pageable) {
        return noticeService.pageAllNoticeOfUser(pageable);
    }
}
