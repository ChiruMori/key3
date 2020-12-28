package work.cxlm.controller.mini.api;

import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.*;
import work.cxlm.model.dto.JoiningDTO;
import work.cxlm.service.JoiningService;

/**
 * created 2020/11/22 17:14
 *
 * @author Chiru
 */
@RestController
@RequestMapping("/key3/joining/api/")
public class JoiningController {

    private final JoiningService joiningService;

    public JoiningController(JoiningService joiningService) {
        this.joiningService = joiningService;
    }

    @ApiOperation("获取某用户加入某社团产生的信息")
    @GetMapping("info/{clubId}/{uid}")
    public JoiningDTO getUserJoining(@PathVariable("clubId") Integer clubId, @PathVariable("uid") Integer uid) {
        return new JoiningDTO().convertFrom(joiningService.getByIds(uid, clubId));
    }

}
