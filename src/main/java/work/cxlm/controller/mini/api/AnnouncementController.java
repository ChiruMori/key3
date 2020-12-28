package work.cxlm.controller.mini.api;

import io.swagger.annotations.ApiOperation;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import work.cxlm.model.dto.AnnouncementDTO;
import work.cxlm.service.AnnouncementService;

/**
 * created 2020/12/22 11:41
 *
 * @author Chiru
 */
@RestController
@RequestMapping("/key3/announcement/api")
public class AnnouncementController {

    private final AnnouncementService announcementService;

    public AnnouncementController(AnnouncementService announcementService) {
        this.announcementService = announcementService;
    }

    @GetMapping("/{annoId}")
    @ApiOperation("获取指定 id 的公告详情")
    public AnnouncementDTO getOne(@PathVariable("annoId") Integer annoId) {
        return announcementService.getAnnouncementDtoById(annoId);
    }

    @GetMapping("/club/{clubId}")
    @ApiOperation("获取某社团的全部公告")
    public Page<AnnouncementDTO> getClubAnnouncements(@PathVariable("clubId") Integer clubId,
                                                      @PageableDefault(sort = "createTime", direction = Sort.Direction.DESC) Pageable pageable) {
        return announcementService.pageClubAnnouncements(clubId, pageable);
    }
}
