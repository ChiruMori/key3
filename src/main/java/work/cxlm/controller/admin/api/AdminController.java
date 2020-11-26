package work.cxlm.controller.admin.api;

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.*;
import work.cxlm.cache.lock.CacheLock;
import work.cxlm.model.dto.UserDTO;
import work.cxlm.model.params.LoginParam;
import work.cxlm.model.params.AuthorityParam;
import work.cxlm.model.params.UserParam;
import work.cxlm.model.vo.DashboardVO;
import work.cxlm.model.vo.PageUserVO;
import work.cxlm.security.token.AuthToken;
import work.cxlm.service.AdminService;
import work.cxlm.service.UserService;

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
    private final UserService userService;

    public AdminController(AdminService adminService, UserService userService) {
        this.adminService = adminService;
        this.userService = userService;
    }

    @PostMapping("login")
    @ApiOperation("管理员登录")
    @CacheLock(autoDelete = false, prefix = "admin_login")
    public AuthToken authLogin(@RequestBody @Valid LoginParam loginParam) {
        return adminService.authenticate(loginParam);
    }

    @GetMapping("logout")
    @ApiOperation("登出")
    @CacheLock(autoDelete = false)
    public void logout() {
        adminService.clearToken();
    }

    @PostMapping("refresh/{refreshToken}")
    @ApiOperation("刷新登录凭证")
    @CacheLock(autoDelete = false)
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

    @PutMapping("user")
    @ApiOperation("管理员更新用户信息")
    public void updateUser(@Valid @RequestBody UserParam userParam) {
        adminService.updateBy(userParam);
    }

    @PostMapping("user")
    @ApiOperation("管理员新建用户")
    public void newUser(@Valid @RequestBody UserParam userParam) {
        adminService.createBy(userParam);
    }

    @DeleteMapping("user/{userId}")
    @ApiOperation("系统管理员删除用户，会销毁改用户相关的全部信息，包括历史信息")
    public void deleteUser(@PathVariable("userId") Integer userId) {
        adminService.delete(userId);
    }

    @GetMapping("user/list/{clubId}")
    @ApiOperation("分页获取社团成员列表，与 User 接口用法、参数相同")
    public Page<PageUserVO> pageClubUsers(@ApiParam(value = "社团 ID", required = true, example = "1") @PathVariable("clubId") Integer clubId,
                                          @PageableDefault(sort = "total", direction = Sort.Direction.DESC) Pageable pageable) {
        return userService.getClubUserPage(clubId, pageable);
    }

    @GetMapping("user/listAll")
    @ApiOperation("分页获取全部用户信息")
    public Page<UserDTO> pageUsers(@PageableDefault(sort = "studentNo", direction = Sort.Direction.DESC) Pageable pageable) {
        return adminService.pageUsers(pageable);
    }

    @GetMapping("dashboard/{clubId}")
    @ApiOperation("请求仪表盘页面需要的数据")
    public DashboardVO getDashboardData(@PathVariable("clubId") Integer clubId) {
        return adminService.dashboardDataOf(clubId);
    }

}
