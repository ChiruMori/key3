package work.cxlm.controller.admin.api;

import com.alibaba.excel.EasyExcel;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import work.cxlm.model.dto.JoiningDTO;
import work.cxlm.model.params.JoiningParam;
import work.cxlm.service.JoiningService;

import java.io.IOException;
import java.util.List;

/**
 * 成员管理、成员相关信息统计等
 * created 2020/11/21 17:14
 *
 * @author Chiru
 */
@RestController
@RequestMapping("/key3/admin/api/")
public class AdminJoiningController {

    private final JoiningService joiningService;

    public AdminJoiningController(JoiningService joiningService) {
        this.joiningService = joiningService;
    }

    @ApiOperation("加入社团")
    @PostMapping("joining")
    public JoiningDTO addMember(@RequestBody JoiningParam param) {
        return joiningService.newJoiningBy(param);
    }

    @ApiOperation("退出社团")
    @DeleteMapping("joining/{clubId:\\d+}/{studentNo}")
    public JoiningDTO removeMember(@PathVariable("clubId") Integer clubId, @PathVariable("studentNo") Long studentNo) {
        return joiningService.removeMember(clubId, studentNo);
    }

    @ApiOperation("获取某社团的全部成员加入信息")
    @GetMapping("joining/{clubId:\\d+}")
    public List<JoiningDTO> listAllJoining(@PathVariable Integer clubId) {
        return joiningService.listAllJoiningDtosByClubId(clubId);
    }

    @ApiOperation("更新社团加入信息")
    @PutMapping("joining")
    public JoiningDTO updateJoining(@RequestBody JoiningParam param) {
        return joiningService.updateJoiningBy(param);
    }

    @ApiOperation("文件导入")
    @PostMapping("joining/{clubId:\\d+}/import")
    public List<String> excelImport(@RequestParam("file") MultipartFile file, @PathVariable Integer clubId) throws IOException {
        return joiningService.importJoiningByFile(file, clubId);
    }
}
