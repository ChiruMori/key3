package work.cxlm.controller.admin.content;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;


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

    /**
     * 管理控制台
     */
    @GetMapping("dashboard")
    public String index(String token, Model model) {

        return "pages/index";
    }

    @GetMapping("login")
    public String login() {
        return "pages/login";
    }

}
