package work.cxlm.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import work.cxlm.model.dto.UserDTO;
import work.cxlm.model.entity.Club;
import work.cxlm.model.entity.User;
import work.cxlm.model.params.LoginParam;
import work.cxlm.model.params.AuthorityParam;
import work.cxlm.model.params.UserParam;
import work.cxlm.model.vo.DashboardVO;
import work.cxlm.security.token.AuthToken;

import java.util.List;

/**
 * created 2020/10/21 14:49
 *
 * @author johnniang
 * @author ryanwang
 * @author cxlm
 */
@Service
public interface AdminService {
    /**
     * ACCESS TOKEN 时效，一天
     */
    int ACCESS_TOKEN_EXPIRED_SECONDS = 3600 * 24;

    /**
     * 30 天刷新凭证失效
     */
    int REFRESH_TOKEN_EXPIRED_DAYS = 30;

    String ADMIN_AUTH_KEY_PREFIX = "admin_auth.";

    /**
     * 管理员登录后台
     *
     * @param loginParam 登录表单参数
     * @return AuthToken 登录凭证
     */
    @NonNull
    AuthToken authenticate(@NonNull LoginParam loginParam);

    /**
     * 清除用户授权
     */
    void clearToken();

    /**
     * 刷新 token.
     *
     * @param refreshToken 新的 token
     * @return AuthToken 刷新后的实例
     */
    @NonNull
    AuthToken refreshToken(@NonNull String refreshToken);

    /**
     * 为用户授予管理员权限
     *
     * @param param 授权变更参数
     */
    @Transactional(rollbackFor = Exception.class)
    void grant(AuthorityParam param);

    /**
     * 收回用户权限
     *
     * @param param 授权变更参数
     */
    @Transactional(rollbackFor = Exception.class)
    void revoke(AuthorityParam param);

    /**
     * 使用用户信息表单更新用户信息
     *
     * @param userParam 用户信息表单
     * @return 更新后的用户数据
     */
    UserDTO updateBy(@NonNull UserParam userParam);

    /**
     * 使用用户信息表单创建用户
     *
     * @param userParam 用户信息表单
     * @return 新建的用户数据
     */
    UserDTO createBy(@NonNull UserParam userParam);

    /**
     * 清除与该用户相关的全部信息
     *
     * @param userId 用户 ID
     * @return 被删除的用户数据
     */
    @Transactional(rollbackFor = Exception.class)
    UserDTO delete(@NonNull Integer userId);

    /**
     * 查询所有用户
     *
     * @param pageable 分页参数
     * @return 用户的分页数据集
     */
    Page<UserDTO> pageUsers(Pageable pageable);

    /**
     * 列出用户管理的所有社团
     *
     * @param admin 管理员用户实例
     * @return 管理员管理的全部社团
     */
    List<Club> listManagedClubs(User admin);

    /**
     * 获取仪表盘页面数据
     *
     * @param clubId 仪表盘当前显示的社团 ID
     * @return 仪表盘显示数据实例
     */
    DashboardVO dashboardDataOf(Integer clubId);

    /**
     * 列出指定社团的全部成员（不一定包含系统管理员）
     *
     * @param clubId 指定社团 ID
     * @return 指定社团的全部用户数据列表
     */
    List<UserDTO> listClubUsers(Integer clubId);
}
