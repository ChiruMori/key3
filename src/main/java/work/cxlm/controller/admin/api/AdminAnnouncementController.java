package work.cxlm.controller.admin.api;

import io.swagger.annotations.ApiOperation;
import org.springframework.lang.NonNull;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import work.cxlm.model.dto.AnnouncementDTO;
import work.cxlm.model.params.AnnouncementParam;
import work.cxlm.service.AnnouncementService;

import java.util.List;

/**
 * created 2020/12/15 20:25
 *
 * @author Chiru
 */
@RestController
@RequestMapping("/key3/admin/api/")
public class AdminAnnouncementController {

    private final AnnouncementService announcementService;

    public AdminAnnouncementController(AnnouncementService announcementService) {
        this.announcementService = announcementService;
    }

    @GetMapping("/announcement/{clubId}")
    @ApiOperation("获取社团全部公告")
    public List<AnnouncementDTO> listAllAnnouncements(@PathVariable("clubId") Integer clubId) {
        return announcementService.getClubAnnouncements(clubId);
    }

    @PostMapping("/announcement")
    @ApiOperation("通过填写表单发布公告")
    public AnnouncementDTO createBy(@Validated @RequestBody AnnouncementParam param) {
        return announcementService.createBy(param);
    }

    @PutMapping("/announcement")
    @ApiOperation("通过填写表单更新公告内容")
    public AnnouncementDTO updateBy(@Validated @RequestBody AnnouncementParam param) {
        return announcementService.updateBy(param);
    }

    @DeleteMapping("/announcement/{annoId:\\d+}")
    @ApiOperation("删除指定 ID 的公告")
    public AnnouncementDTO deleteOne(@PathVariable Integer annoId) {
        return announcementService.deleteOne(annoId);
    }
}
