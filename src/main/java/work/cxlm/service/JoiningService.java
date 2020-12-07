package work.cxlm.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import org.springframework.transaction.annotation.Transactional;
import work.cxlm.model.dto.JoiningDTO;
import work.cxlm.model.entity.Club;
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
     * 根据社团 ID 列出全部 Joining 实体
     */
    @NonNull
    Page<Joining> pageAllJoiningByClubId(@Nullable Integer clubId, Pageable pageable);

    /**
     * 根据用户 ID 列出 Joining 实体分页
     */
    @NonNull
    Page<Joining> pageAllJoiningByUserId(@Nullable Integer userId, Pageable pageable);

    List<Joining> listAllJoiningByUserId(@Nullable Integer userId);

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
     * @param admin 是否为社团管理员
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
     * 管理员通过填写表单添加社团成员
     */
    @Transactional
    JoiningDTO newJoiningBy(JoiningParam param);

    /**
     * 管理员通过填写表单删除社团成员，同时会删除对活动室时段的占用
     */
    @Transactional
    JoiningDTO removeMember(@NonNull Integer clubId, @NonNull Long studentNo);

    /**
     * 删除某用户的全部社团信息
     */
    void deleteByUserId(Integer userId);

    /**
     * 获取指定社团的所有 Joining 实体
     */
    List<Joining> listAllJoiningByClubId(Integer clubId);

    /**
     * 删除社团相关的全部信息
     * @param clubId 社团 ID
     */
    void removeByIdClubId(Integer clubId);

    /**
     * 获取指定社团的所有 JoiningDTO 实体
     */
    List<JoiningDTO> listAllJoiningDTOByClubId(Integer clubId);

    /**
     * 更新用户加入信息
     */
    @Transactional
    JoiningDTO updateJoiningBy(JoiningParam param);

    @NonNull
    Joining getByIds(@NonNull Integer userId, @NonNull Integer clubId);

    /**
     * 判断指定的用户是否管理了某个社团
     */
    boolean adminOfAny(@NonNull User targetUser);

    /**
     * 用userId判断用户参加了哪些社团
     */
    @NonNull
    List<Club> getUserClubs(@NonNull Integer userId);
}
