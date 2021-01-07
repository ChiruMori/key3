package work.cxlm.controller.admin.content;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * created 2021/1/6 20:52
 *
 * @author Chiru
 */
@Controller
@RequestMapping("/key3/kit/page/")
public class KitController {

    @GetMapping("kit-index")
    public String kitIndex() {
        return "pages/kit-index";
    }

    @GetMapping("kit-log")
    public String kitLog() {
        return "pages/kit-log";
    }

    @GetMapping("kit-cache")
    public String kitCache() {
        return "pages/kit-cache";
    }
}
