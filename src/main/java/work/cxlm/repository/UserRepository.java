package work.cxlm.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import work.cxlm.model.entity.User;
import work.cxlm.model.enums.UserRole;

import java.util.List;
import java.util.Optional;

/**
 * created 2020/10/22 16:03
 *
 * @author johnniang
 * @author cxlm
 */
public interface UserRepository extends BaseRepository<User, Integer> {
//    /**
//     * @param realName 用户名，不可为 null
//     * @return Optional 包装的 User 实例
//     */
//    @NonNull
//    Optional<User> findByRealName(@NonNull String realName);

    /**
     * 通过用户学工号查找已有的用户信息
     *
     * @param studentNo 学工号
     */
    @NonNull
    Optional<User> findByStudentNo(@NonNull Long studentNo);

    /**
     * @param openId openId，从小程序得到的唯一标识
     * @return Optional 包装的 User 实例
     */
    @NonNull
    Optional<User> findByWxId(@NonNull String openId);

    @NonNull
    Page<User> findAllBy(@NonNull Pageable pageable);

    @NonNull
    List<User> findAllByRole(@NonNull UserRole role);
}
