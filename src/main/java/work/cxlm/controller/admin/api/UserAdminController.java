package work.cxlm.controller.admin.api;

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.*;
import work.cxlm.model.dto.LogDTO;
import work.cxlm.model.dto.UserDTO;
import work.cxlm.model.params.UserParam;
import work.cxlm.model.vo.PageUserVO;
import work.cxlm.service.AdminService;
import work.cxlm.service.UserService;

import javax.validation.Valid;
import java.util.List;

/**
 * created 2020/11/30 22:32
 *
 * @author Chiru
 */
@RestController
@RequestMapping("/key3/admin/api/")
public class UserAdminController {

    private final UserService userService;
    private final AdminService adminService;

    public UserAdminController(UserService userService,
                               AdminService adminService) {
        this.userService = userService;
        this.adminService = adminService;
    }

    @GetMapping("user/all/{clubId:\\d+}")
    @ApiOperation("管理员获取某社团成员列表，成员不一定包含系统管理员")
    public List<UserDTO> getAllLogs(@PathVariable Integer clubId) {
        return adminService.listClubUsers(clubId);
    }

    @PutMapping("user")
    @ApiOperation("管理员更新用户信息")
    public UserDTO updateUser(@Valid @RequestBody UserParam userParam) {
        return adminService.updateBy(userParam);
    }

    @PostMapping("user")
    @ApiOperation("管理员新建用户，新建的用户与社团没有任何关系")
    public UserDTO newUser(@Valid @RequestBody UserParam userParam) {
        return adminService.createBy(userParam);
    }

    @DeleteMapping("user/{userId:\\d+}")
    @ApiOperation("系统管理员删除用户，会销毁改用户相关的全部信息，包括历史信息")
    public UserDTO deleteUser(@PathVariable("userId") Integer userId) {
        return adminService.delete(userId);
    }

    @GetMapping("user/list/{clubId:\\d+}")
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

}
