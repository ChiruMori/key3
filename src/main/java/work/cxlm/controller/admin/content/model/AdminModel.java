package work.cxlm.controller.admin.content.model;

import org.springframework.stereotype.Component;
import org.springframework.ui.Model;
import work.cxlm.exception.ForbiddenException;
import work.cxlm.model.entity.User;
import work.cxlm.model.enums.UserRole;
import work.cxlm.model.vo.AdminBaseVO;
import work.cxlm.security.context.SecurityContextHolder;
import work.cxlm.service.AdminService;
import work.cxlm.service.BillService;
import work.cxlm.service.LogService;
import work.cxlm.service.UserService;

/**
 * 管理员页面数据的包装工具类
 * created 2020/11/26 9:01
 *
 * @author Chiru
 */
@Component
public class AdminModel {

    private final AdminService adminService;

    public AdminModel(AdminService adminService) {
        this.adminService = adminService;
    }

    /**
     * 包装基本数据（侧边栏、顶部导航栏需要的数据）
     *
     * @param model         视图层数据传输对象
     * @param dashboardPage 是否为仪表盘页面（仪表盘页面需要显示提示卡片）
     */
    public void wrapBaseData(Model model, boolean dashboardPage) {
        AdminBaseVO baseVO = new AdminBaseVO();
        User admin = SecurityContextHolder.ensureUser();
        UserRole role = admin.getRole();
        if (role.isNormalRole()) {
            throw new ForbiddenException("权限不足，无法使用管理后台");
        }
        baseVO.setHead(admin.getHead());
        baseVO.setShowSideBarCard(dashboardPage);
        baseVO.setUserName(admin.getRealName());
        baseVO.setSystemAdmin(role.isSystemAdmin());
        baseVO.setClubAdmin(role.isAdminRole());
        baseVO.setClubs(adminService.listManagedClubs(admin));
        model.addAttribute("base", baseVO);
    }
}
