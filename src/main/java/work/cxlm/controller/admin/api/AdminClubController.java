package work.cxlm.controller.admin.api;

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.web.bind.annotation.*;
import work.cxlm.model.dto.ClubDTO;
import work.cxlm.model.params.ClubParam;
import work.cxlm.service.ClubService;

import javax.validation.Valid;

/**
 * 社团管理接口
 * created 2020/11/27 15:00
 *
 * @author Chiru
 */
@RestController
@RequestMapping("/key3/admin/api/")
public class AdminClubController {

    private final ClubService clubService;

    public AdminClubController(ClubService clubService) {
        this.clubService = clubService;
    }

    @GetMapping("/club/{clubId:\\d+}")
    @ApiOperation("获取社团信息")
    public ClubDTO clubInfo (@ApiParam("社团 ID") @PathVariable("clubId") Integer clubId) {
        return clubService.getManagedClubInfo(clubId);
    }

    @PostMapping("/club")
    @ApiOperation("系统管理员创建社团")
    public ClubDTO newClub(@RequestBody ClubParam clubParam) {
        return clubService.newClubByParam(clubParam);
    }

    @PutMapping("/club")
    @ApiOperation("管理员修改社团信息")
    public ClubDTO updateClub(@RequestBody ClubParam clubParam) {
        return clubService.updateByParam(clubParam);
    }

    @DeleteMapping("/club/{clubId:\\d+}")
    @ApiOperation("删除社团信息，会级联删除对应的用户加入信息、财务信息、公告信息、活动室归属信息，需要给用户强烈提示")
    public void deleteClub (@ApiParam("社团 ID") @PathVariable("clubId") Integer clubId) {
        clubService.deleteClub(clubId);
    }
}
