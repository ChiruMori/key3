package work.cxlm.controller.mini.api;

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import work.cxlm.cache.lock.CacheLock;
import work.cxlm.model.dto.UserDTO;
import work.cxlm.model.entity.User;
import work.cxlm.model.params.UserLoginParam;
import work.cxlm.model.params.UserParam;
import work.cxlm.model.vo.PageUserVO;
import work.cxlm.model.vo.PasscodeVO;
import work.cxlm.security.context.SecurityContextHolder;
import work.cxlm.security.token.AuthToken;
import work.cxlm.service.UserService;

import javax.validation.Valid;

import static work.cxlm.model.support.QfzsConst.USER_HELP;

/**
 * created 2020/11/15 10:45
 *
 * @author johnniang
 * @author Chiru
 */
@RestController
@RequestMapping("/key3/users/api")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @ApiOperation(value = "获取用户信息", notes = "获取用户详细信息，包含用户的全部字段\n需要 accessToken 且必填，标识用户会话")
    @GetMapping("/info")
    public UserDTO getProfile() {
        User nowUser = SecurityContextHolder.ensureUser();
        return new UserDTO().convertFrom(nowUser);
    }

    @ApiOperation(value = "获取用户信息", notes = "获取指定用户的详细信息，包含用户的全部字段\n需要 accessToken 且必填，标识用户会话")
    @GetMapping("/info/{userId}")
    public UserDTO getUserInfo(@PathVariable("userId") Integer uid) {
        return new UserDTO().convertFrom(userService.getById(uid));
    }

    @ApiOperation(value = "更新用户信息", notes = "用户与微信相关的信息，在本方法调用后完成绑定\n需要 accessToken ，首次更新可缺省，标识用户会话")
    @PutMapping("/update")
    public UserDTO uploadUserInfo(@RequestBody @Validated UserParam userParam) {
        return new UserDTO().convertFrom(userService.updateUserByParam(userParam));
    }

    @ApiOperation(value = "用户登录", notes = "必须传递 code 登录成功将会获得用户登录凭证（accessToken），如果用户不存在则得到字段均为 null 的响应")
    @PostMapping("/login")
    @CacheLock(prefix = "user_token", msg = "正在登录人数过多，请重试")
    public AuthToken userLogin(@Valid @RequestBody UserLoginParam userLoginParam) {
        String openId = userService.getOpenIdBy(userLoginParam);
        return userService.login(openId);
    }

    @PostMapping("/refresh/{refreshToken}")
    @ApiOperation("刷新用户凭证过期时间，需要使用 refreshToken 进行刷新")
    @CacheLock(prefix = "user_token", msg = "正在登录人数过多，请重试")
    public AuthToken refresh(@PathVariable("refreshToken") String refreshToken) {
        return userService.refreshToken(refreshToken, StringUtils.EMPTY, User::getWxId, userService::getByOpenId, String.class);
    }

    @ApiOperation(value = "获取本社团的用户列表", notes = "注意分页处理与参数\n需要 accessToken 且必填，标识用户会话")
    @GetMapping("/list/{clubId:\\d+}")
    public Page<PageUserVO> pageMembersBy(@ApiParam(value = "社团 ID", required = true, example = "1") @PathVariable("clubId") Integer clubId,
                                          @PageableDefault(sort = {"total", "idUserId"}, direction = Sort.Direction.DESC) Pageable pageable) {
        return userService.getClubUserPage(clubId, pageable);
    }

    @ApiOperation(value = "获取一次性 passcode", notes = "passcode 为管理员登录管理后台时使用，只应该在管理员用户中调用，普通用户调用则得到异常。\n需要 accessToken 且必填，标识用户会话")
    @GetMapping("/code")
    public PasscodeVO getAdminPasscode() {
        return userService.getPasscode();
    }

    @ApiOperation(value = "获取用户帮助文档，得到的数据为二维字符串数组")
    @GetMapping("/help")
    public String[][] getHelpInfo() {
        return USER_HELP;
    }

}
