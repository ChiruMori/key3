package work.cxlm.security.authentication;

import org.springframework.lang.NonNull;
import work.cxlm.security.support.UserDetail;

/**
 * 用户登录认证
 * created 2020/11/9 9:23
 *
 * @author Chiru
 */
public interface Authentication {

    /**
     * 获取用户详情（UserDetail 实例）
     *
     * @return 被包装的 UserDetail 实体
     */
    @NonNull
    UserDetail getUserDetail();
}
