package work.cxlm.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import work.cxlm.model.dto.UserDTO;
import work.cxlm.model.params.LoginParam;
import work.cxlm.model.params.AuthorityParam;
import work.cxlm.model.params.UserParam;
import work.cxlm.security.token.AuthToken;

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
    int ACCESS_TOKEN_EXPIRED_SECONDS = 24 * 3600;

    int REFRESH_TOKEN_EXPIRED_DAYS = 30;

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
    void updateBy(@NonNull UserParam userParam);

    /**
     * 使用用户信息表单创建用户
     *
     * @param userParam 用户信息表单
     */
    void createBy(@NonNull UserParam userParam);

    /**
     * 清除与该用户相关的全部信息
     */
    @Transactional
    void delete(@NonNull Integer userId);

    /**
     * 查询所有用户
     */
    Page<UserDTO> pageUsers(Pageable pageable);
}
