package work.cxlm.controller.admin.content;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import work.cxlm.controller.admin.content.model.AdminModel;
import work.cxlm.service.AdminService;


/**
 * 管理后台页面 Controller
 * created 2020/11/25 14:32
 *
 * @author Chiru
 */
@Slf4j
@Controller
@RequestMapping("/key3/admin/page/")
public class ContentController {

    private final AdminService adminService;
    private final AdminModel adminModel;

    public ContentController(AdminService adminService, AdminModel adminModel) {
        this.adminService = adminService;
        this.adminModel = adminModel;
    }

    /**
     * 管理控制台
     */
    @GetMapping("dashboard")
    public String index(Model model) {
        adminModel.wrapBaseData(model, true);
        return "pages/index";
    }

    @GetMapping("login")
    public String login() {
        return "pages/login";
    }

    @GetMapping("club-info")
    public String clubInfo(Model model) {
        adminModel.wrapBaseData(model, false);
        return "pages/club-info";
    }

    @GetMapping("club-bill")
    public String clubBill(Model model) {
        adminModel.wrapBaseData(model, false);
        return "pages/club-bill";
    }


}
