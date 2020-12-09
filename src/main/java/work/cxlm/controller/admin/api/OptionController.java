package work.cxlm.controller.admin.api;

import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.*;
import work.cxlm.mail.MailService;
import work.cxlm.model.support.BaseResponse;
import work.cxlm.service.OptionService;

import javax.validation.Valid;
import java.util.Map;

/**
 * created 2020/12/9 18:09
 *
 * @author Chiru
 * @author johnniang
 * @author ryanwang
 */
@RestController
@RequestMapping("/key3/admin/api/options/")
public class OptionController {

    private final OptionService optionService;
    private final MailService mailService;

    public OptionController(OptionService optionService, MailService mailService) {
        this.optionService = optionService;
        this.mailService = mailService;
    }

    @GetMapping("map_view")
    @ApiOperation("按照 Map 的方式列出所有参数")
    public Map<String, Object> listAllWithMapView() {
        return optionService.listOptions();
    }

    @PutMapping("map_view")
    @ApiOperation("通过 Map 保存一组配置项")
    public void saveOptionsWithMapView(@RequestBody Map<String, Object> optionMap) {
        optionService.save(optionMap);
    }

    @PostMapping("mail_test")
    @ApiOperation("管理员在调整 SMTP 参数后，可以使用本接口进行测试")
    public BaseResponse<String> testMail(@RequestParam("addr") String addr) {
        mailService.sendTextMail(addr, "测试邮件", "如果您收到此消息，说明您的系统邮件配置项正确。如果您不知道这是什么，请忽略本邮件。");
        return BaseResponse.ok("邮件已发送，请查收");
    }
}
