package work.cxlm.controller.admin.api;

import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.*;
import work.cxlm.model.params.LoginParam;
import work.cxlm.model.params.AuthorityParam;
import work.cxlm.model.vo.DashboardVO;
import work.cxlm.security.token.AuthToken;
import work.cxlm.service.AdminService;

import javax.validation.Valid;

/**
 * created 2020/10/21 14:31
 *
 * @author cxlm
 */
@RestController
@RequestMapping("/key3/admin/api/")
public class AdminController {

    private final AdminService adminService;

    public AdminController(AdminService adminService) {
        this.adminService = adminService;
    }

    @PostMapping("login")
    @ApiOperation("管理员登录")
    // 针对管理员的学号加锁，非阻塞锁，重复获取锁时失败，方法结束后自动解锁，防止一个账号在多处同时登陆时获得立即失效的 token
    public AuthToken authLogin(@RequestBody @Valid LoginParam loginParam) {
        return adminService.authenticate(loginParam);
    }

    @GetMapping("logout")
    @ApiOperation("登出")
    // @CacheLock(prefix = "admin_token")，无需加锁
    public void logout() {
        adminService.clearToken();
    }

    @PostMapping("refresh/{refreshToken}")
    @ApiOperation("刷新登录凭证")
    // 针对管理员 token 加锁，非阻塞锁，重复获取锁时失败，方法结束后自动解锁，防止 token 在多处进行刷新时获得立即失效的 token
    public AuthToken refresh(@PathVariable("refreshToken") String refreshToken) {
        return adminService.refreshToken(refreshToken);
    }

    @PostMapping("authority")
    @ApiOperation("为指定学号的用户授权，授权的角色、管理的社团需要通过参数指定")
    public void grant(@Valid @RequestBody AuthorityParam param) {
        adminService.grant(param);
    }

    @PutMapping("authority")
    @ApiOperation("收回指定学号的用户授权，授权的角色、管理的社团需要通过参数指定")
    public void revoke(@Valid @RequestBody AuthorityParam param) {
        adminService.revoke(param);
    }

    @GetMapping("dashboard/{clubId:\\d+}")
    @ApiOperation("请求仪表盘页面需要的数据")
    public DashboardVO getDashboardData(@PathVariable("clubId") Integer clubId) {
        return adminService.dashboardDataOf(clubId);
    }

}
