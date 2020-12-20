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
     * ACCESS TOKEN 时效
     */
    int ACCESS_TOKEN_EXPIRED_SECONDS = 3600 * 24;  // 一天登录凭证失效

    int REFRESH_TOKEN_EXPIRED_DAYS = 30;  // 30 天刷新凭证失效

    String ADMIN_AUTH_KEY_PREFIX = "admin_auth.";

    /**
     * 管理员登录后台
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
     */
    @Transactional
    void grant(AuthorityParam param);

    /**
     * 收回用户权限
     */
    @Transactional
    void revoke(AuthorityParam param);

    /**
     * 使用用户信息表单更新用户信息
     *
     * @param userParam 用户信息表单
     */
    UserDTO updateBy(@NonNull UserParam userParam);

    /**
     * 使用用户信息表单创建用户
     *
     * @param userParam 用户信息表单
     */
    UserDTO createBy(@NonNull UserParam userParam);

    /**
     * 清除与该用户相关的全部信息
     */
    @Transactional
    UserDTO delete(@NonNull Integer userId);

    /**
     * 查询所有用户
     */
    Page<UserDTO> pageUsers(Pageable pageable);

    /**
     * 列出用户管理的所有社团
     */
    List<Club> listManagedClubs(User admin);

    /**
     * 获取仪表盘页面数据
     */
    DashboardVO dashboardDataOf(Integer clubId);

    /**
     * 列出指定社团的全部成员（不一定包含系统管理员）
     */
    List<UserDTO> listClubUsers(Integer clubId);
}
