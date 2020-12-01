package work.cxlm.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import work.cxlm.exception.NotFoundException;
import work.cxlm.model.entity.Club;
import work.cxlm.model.entity.User;
import work.cxlm.model.params.UserLoginParam;
import work.cxlm.model.params.UserParam;
import work.cxlm.model.vo.PageUserVO;
import work.cxlm.model.vo.PasscodeVO;
import work.cxlm.security.token.AuthToken;
import work.cxlm.service.base.CrudService;

import java.util.Map;
import java.util.Optional;
import java.util.function.Function;

/**
 * created 2020/10/21 16:01
 *
 * @author johnniang
 * @author ryanwang
 * @author cxlm
 */
public interface UserService extends CrudService<User, Integer> {

    /**
     * 通过用户登录凭证获取 openId
     *
     * @param code 登录凭证
     * @return openId，可能为 null
     */
    @Nullable
    String getOpenIdByCode(@NonNull String code);

    /**
     * 用户使用 openId 登录，通过小程序登录
     *
     * @param openId 小程序中得到的 openId，用户唯一标识
     * @return 登录后的用户凭证
     */
    @Nullable
    AuthToken login(@Nullable String openId);

    /**
     * 为用户创建合法登录凭证
     */
    <T> AuthToken buildAuthToken(@NonNull User user, Function<User, T> converter);

    /**
     * 通过表单更新用户信息
     *
     * @param param 填写的表单
     * @return 更新后的用户，如果找不到对应的用户将返回 null
     */
    @Nullable
    User updateUserByParam(@NonNull UserParam param);

    @NonNull
    Page<PageUserVO> getClubUserPage(@Nullable Integer clubId, Pageable pageable);

    /**
     * 使用用户创建 Passcode 传输对象
     */
    @NonNull
    PasscodeVO getPasscode();

    /**
     * 刷新用户登录凭证到期事件
     */
    <T> AuthToken refreshToken(String refreshToken, Function<User, T> idGetter, Function<T, User> userGetter, Class<T> idType);

    /**
     * 通过 openId 查询用户信息
     */
    User getByOpenId(String integer);

    /**
     * 通过学号查找用户
     */
    Optional<User> getByStudentNo(Long studentNo);

    /**
     * 判断用户是否为某社团的管理员
     *
     * @param userId 用户 id
     * @param club   社团
     * @return 布尔值表示结果
     */
    boolean managerOfClub(@NonNull Integer userId, @NonNull Club club);
    boolean managerOfClub(@NonNull User admin, @NonNull Club club);
    boolean managerOfClub(User admin, Integer clubId);

    /**
     * 判断用户是否为另一用户的管理员
     *
     * @param userId 当前用户（准管理员）
     * @param other  另一用户
     */
    boolean managerOf(@NonNull Integer userId, @NonNull User other);

    /**
     * 判断用户是否为另一用户的管理员
     *
     * @param admin 当前用户（准管理员）
     * @param other 另一用户
     */
    boolean managerOf(@NonNull User admin, @NonNull User other);

    /**
     * 获取全部的用户信息，以 id, user 映射的方式返回
     */
    Map<Integer, User> getAllUserMap();

}
