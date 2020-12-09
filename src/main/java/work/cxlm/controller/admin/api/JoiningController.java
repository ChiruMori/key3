package work.cxlm.controller.admin.api;

import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.*;
import work.cxlm.model.dto.JoiningDTO;
import work.cxlm.model.params.JoiningParam;
import work.cxlm.service.JoiningService;

import java.util.List;

/**
 * 成员管理、成员相关信息统计等
 * created 2020/11/21 17:14
 *
 * @author Chiru
 */
@RestController
@RequestMapping("/key3/admin/api/")
public class JoiningController {

    private final JoiningService joiningService;

    public JoiningController(JoiningService joiningService) {
        this.joiningService = joiningService;
    }

    @ApiOperation("加入社团")
    @PostMapping("joining")
    public JoiningDTO addMember(@RequestBody JoiningParam param) {
        return joiningService.newJoiningBy(param);
    }

    @ApiOperation("退出社团")
    @DeleteMapping("joining/{clubId:\\d+}/{studentNo:\\d+}")
    public JoiningDTO removeMember(@PathVariable("clubId") Integer clubId, @PathVariable("studentNo") Long studentNo) {
        return joiningService.removeMember(clubId, studentNo);
    }

    @ApiOperation("获取某社团的全部成员加入信息")
    @GetMapping("joining/{clubId:\\d+}")
    public List<JoiningDTO> listAllJoining(@PathVariable Integer clubId) {
        return joiningService.listAllJoiningDTOByClubId(clubId);
    }

    @ApiOperation("更新社团加入信息")
    @PutMapping("joining")
    public JoiningDTO updateJoining(@RequestBody JoiningParam param) {
        return joiningService.updateJoiningBy(param);
    }

}
