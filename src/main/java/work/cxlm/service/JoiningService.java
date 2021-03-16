package work.cxlm.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import org.springframework.transaction.annotation.Transactional;
import work.cxlm.model.dto.JoiningDTO;
import work.cxlm.model.entity.Joining;
import work.cxlm.model.entity.User;
import work.cxlm.model.entity.id.JoiningId;
import work.cxlm.model.params.JoiningParam;
import work.cxlm.service.base.CrudService;

import java.util.List;
import java.util.Optional;

/**
 * created 2020/11/18 12:50
 *
 * @author Chiru
 */
public interface JoiningService extends CrudService<Joining, JoiningId> {
    /**
     * 分页列出某社团的全部用户加入信息
     *
     * @param clubId   社团 ID
     * @param pageable 分页参数
     * @return Joining 的分页查询结果集
     */
    @NonNull
    Page<Joining> pageAllJoiningByClubId(@Nullable Integer clubId, Pageable pageable);

    /**
     * 分页列出用户全部的加入实体
     *
     * @param userId   指定的用户 id
     * @param pageable 分页参数
     * @return Joining 的分页查询结果集
     */
    @NonNull
    Page<Joining> pageAllJoiningByUserId(@Nullable Integer userId, Pageable pageable);

    /**
     * 列出某用户的全部社团加入信息
     *
     * @param userId 目标用户 id
     * @return 该用户的 Joining 列表
     */
    List<Joining> listAllJoiningByUserId(@Nullable Integer userId);

    /**
     * 通过 user id、club id 查询用户加入社团产生的信息
     *
     * @param userId 用户 ID
     * @param clubId 社团 ID
     * @return Optional 包装的 Joining 对象
     */
    @NonNull
    Optional<Joining> getJoiningById(@Nullable Integer userId, @Nullable Integer clubId);

    /**
     * 判断用户是否加入某社团
     *
     * @param userId 用户 id
     * @param clubId 社团 id
     * @return 布尔值表示结果
     */
    boolean hasJoined(@NonNull Integer userId, @NonNull Integer clubId);

    /**
     * 未加入该社团时加入
     *
     * @param userId 用户 id
     * @param clubId 社团 id
     * @param admin  是否为社团管理员
     */
    void joinIfAbsent(@NonNull Integer userId, @NonNull Integer clubId, boolean admin);

    /**
     * 收回社团管理员授权
     *
     * @param userId 用户 id
     * @param clubId 社团 id
     */
    void revokeAdmin(@NonNull Integer userId, @NonNull Integer clubId);

    /**
     * 管理员通过管理后台表单添加社团成员
     *
     * @param param 表单实体
     * @return 生成的 Joining DTO 对象
     */
    @Transactional(rollbackFor = Exception.class)
    JoiningDTO newJoiningBy(JoiningParam param);

    /**
     * 管理员删除社团成员，同时会删除对活动室时段的占用
     *
     * @param clubId    指定的社团 id
     * @param studentNo 用户学号
     * @return 删除的 Joining DTO 对象
     */
    @Transactional(rollbackFor = Exception.class)
    JoiningDTO removeMember(@NonNull Integer clubId, @NonNull Long studentNo);

    /**
     * 删除某用户的全部社团加入信息
     *
     * @param userId 指定的用户 ID
     */
    void deleteByUserId(Integer userId);

    /**
     * 列出指定社团的所有 Joining 实体
     *
     * @param clubId 指定的社团 id
     * @return 该社团全部的用户加入信息
     */
    List<Joining> listAllJoiningByClubId(Integer clubId);

    /**
     * 删除社团相关的全部加入信息
     *
     * @param clubId 社团 ID
     */
    void removeByIdClubId(Integer clubId);

    /**
     * 列出指定社团的所有 JoiningDTO 实体
     *
     * @param clubId 指定的社团 id
     * @return 社团全部的 Joining DTO 实体列表
     */
    List<JoiningDTO> listAllJoiningDtosByClubId(Integer clubId);

    /**
     * 更新用户加入信息
     *
     * @param param 管理员在管理后台填写的表单实体
     * @return 更新后的 Joining DTO 实体
     */
    @Transactional(rollbackFor = Exception.class)
    JoiningDTO updateJoiningBy(JoiningParam param);

    /**
     * 通过用户、社团查询用户加入社团产生的信息
     *
     * @param userId 指定用户的 id
     * @param clubId 指定社团的 id
     * @return Joining 实体
     */
    @NonNull
    Joining getByIds(@NonNull Integer userId, @NonNull Integer clubId);

    /**
     * 判断指定的用户是否管理了某个社团（为管理员用户）
     *
     * @param targetUser 指定的用户实体
     * @return 该用户是否为管理员用户角色
     */
    boolean adminOfAny(@NonNull User targetUser);

    /**
     * 根据 userId 集合获取全部 Joining
     *
     * @param userIds userId 列表
     * @return 用户 id 列表关联的全部 Joining 实例
     */
    List<Joining> listAllJoiningByUserIdIn(@NonNull List<Integer> userIds);
}
