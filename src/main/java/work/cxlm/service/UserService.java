package work.cxlm.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import work.cxlm.model.entity.Club;
import work.cxlm.model.entity.User;
import work.cxlm.model.params.UserLoginParam;
import work.cxlm.model.params.UserParam;
import work.cxlm.model.vo.PageUserVO;
import work.cxlm.model.vo.PasscodeVO;
import work.cxlm.security.token.AuthToken;
import work.cxlm.service.base.CrudService;

import java.util.Map;
import java.util.List;
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
     * @param param 登录参数
     * @return openId，可能为 null
     */
    @Nullable
    String getOpenIdBy(@NonNull UserLoginParam param);

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
     *
     * @param user      指定的用户
     * @param keyPrefix 缓存键前缀
     * @param converter 转换器
     * @param <T>       缓存值的类型
     * @return 用于登录授权
     */
    <T> AuthToken buildAuthToken(@NonNull User user, String keyPrefix, Function<User, T> converter);

    /**
     * 清除用户 Token，在创建用户 Token 或者登出时调用
     *
     * @param user      目标用户
     * @param keyPrefix 缓存键前缀
     */
    void clearUserToken(@NonNull User user, String keyPrefix);

    /**
     * 通过表单更新用户信息
     *
     * @param param 填写的表单
     * @return 更新后的用户，如果找不到对应的用户将返回 null
     */
    @Nullable
    User updateUserByParam(@NonNull UserParam param);

    /**
     * 获取社团的用户分页数据集
     *
     * @param clubId   社团 id
     * @param pageable 分页查参数
     * @return 社团用户数据的分页数据集
     */
    @NonNull
    Page<PageUserVO> getClubUserPage(@Nullable Integer clubId, Pageable pageable);

    /**
     * 使用用户创建 Passcode 传输对象
     *
     * @return 登录口令
     */
    @NonNull
    PasscodeVO getPasscode();

    /**
     * 刷新用户登录凭证到期事件
     *
     * @param refreshToken 用户登录凭证刷新凭证
     * @param keyPrefix    缓存键前缀
     * @param idGetter     从用户实体中获取 ID 的函数
     * @param userGetter   根据 ID 查用户的函数
     * @param idType       ID 类对象
     * @param <T>          ID 类型参数
     * @return 用户登录凭证
     */
    <T> AuthToken refreshToken(String refreshToken, String keyPrefix, Function<User, T> idGetter, Function<T, User> userGetter, Class<T> idType);

    /**
     * 通过 openId 查询用户信息
     *
     * @param openId 用户 openId
     * @return 查询到的用户信息
     */
    User getByOpenId(String openId);

    /**
     * 通过学号查找用户
     *
     * @param studentNo 用户学号
     * @return 查询到的用户（Optional 包装）
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

    /**
     * 判断用户是否为某社团的管理员
     *
     * @param admin 用户
     * @param club  社团
     * @return 布尔值表示结果
     */
    boolean managerOfClub(@NonNull User admin, @NonNull Club club);

    /**
     * 判断用户是否为某社团的管理员
     *
     * @param admin  用户
     * @param clubId 社团 ID
     * @return 布尔值表示结果
     */
    boolean managerOfClub(User admin, Integer clubId);

    /**
     * 判断用户是否为另一用户的管理员
     *
     * @param userId 当前用户（准管理员）
     * @param other  另一用户
     * @return 布尔值表示结果
     */
    boolean managerOf(@NonNull Integer userId, @NonNull User other);

    /**
     * 判断用户是否为另一用户的管理员
     *
     * @param admin 当前用户（准管理员）
     * @param other 另一用户
     * @return 布尔值表示结果
     */
    boolean managerOf(@NonNull User admin, @NonNull User other);

    /**
     * 获取全部的用户信息，以 id, user 映射的方式返回
     *
     * @deprecated 尽可能使用缓存替代本方法的使用，除非明确需要全表扫描时
     * @return ID, User 的映射集
     */
    @Deprecated
    Map<Integer, User> getAllUserMap();

    /**
     * 获取社团的全部用户列表
     *
     * @param clubId 指定的社团 id
     * @return 指定社团的全部用户列表
     */
    List<User> getClubUsers(@NonNull Integer clubId);

    /**
     * 通过 openId 查找用户（不经过缓存）
     *
     * @param openId 用户填写的 openId
     * @return 指定的用户
     * @throws work.cxlm.exception.NotFoundException 用户不存在时抛出
     */
    @NonNull
    User getByWxIdIgnoreCache(@NonNull String openId);
}
