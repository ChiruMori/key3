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

    /**
     * 通过用户学工号查找已有的用户信息
     *
     * @param studentNo 学工号
     * @return Optional 包装的用户实体
     */
    @NonNull
    Optional<User> findByStudentNo(@NonNull Long studentNo);

    /**
     * 通过 openId 查找用户
     *
     * @param openId openId，从小程序得到的唯一标识
     * @return Optional 包装的 User 实例
     */
    @NonNull
    Optional<User> findByWxId(@NonNull String openId);

    /**
     * 分页列出全部用户
     *
     * @param pageable 分页参数
     * @return 用户的分页数据集
     */
    @NonNull
    Page<User> findAllBy(@NonNull Pageable pageable);

    /**
     * 列出全部某角色的用户
     *
     * @param role 指定的用户角色
     * @return 拥有该角色的全部用户列表
     */
    @NonNull
    List<User> findAllByRole(@NonNull UserRole role);
}
