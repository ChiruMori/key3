package work.cxlm.security.ott;

import org.springframework.lang.NonNull;

import java.util.Optional;

/**
 * 管理一次性口令的接口
 * created 2020/11/19 16:23
 *
 * @author johnniang
 * @author Chiru
 */
public interface OneTimeTokenService {

    /**
     * 根据 Token 得到对应的 URL
     *
     * @param oneTimeToken 一次性 Token，oot
     * @return Optional 包装的 oot 值，即对应的 URL
     */
    @NonNull
    Optional<String> get(@NonNull String oneTimeToken);

    /**
     * 使用 URL 生成对应的 Token
     *
     * @param uri 构建针对 URI 的 OOT
     * @return 构建的 OOT
     */
    @NonNull
    String create(@NonNull String uri);

    /**
     * 收回发放的 Token
     *
     * @param oneTimeToken 指定的 OOT
     */
    void revoke(@NonNull String oneTimeToken);
}
