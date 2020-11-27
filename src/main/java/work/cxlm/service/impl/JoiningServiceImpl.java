package work.cxlm.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;
import work.cxlm.exception.BadRequestException;
import work.cxlm.exception.ForbiddenException;
import work.cxlm.exception.NotFoundException;
import work.cxlm.model.entity.Club;
import work.cxlm.model.entity.Joining;
import work.cxlm.model.entity.User;
import work.cxlm.model.entity.id.JoiningId;
import work.cxlm.model.enums.UserRole;
import work.cxlm.model.params.NewMemberParam;
import work.cxlm.model.support.CreateCheck;
import work.cxlm.model.support.UpdateCheck;
import work.cxlm.repository.JoiningRepository;
import work.cxlm.security.context.SecurityContextHolder;
import work.cxlm.service.ClubService;
import work.cxlm.service.JoiningService;
import work.cxlm.service.UserService;
import work.cxlm.service.base.AbstractCrudService;
import work.cxlm.utils.ValidationUtils;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

/**
 * created 2020/11/18 13:13
 *
 * @author Chiru
 */
@Service
@Slf4j
public class JoiningServiceImpl extends AbstractCrudService<Joining, JoiningId> implements JoiningService {

    private UserService userService;

    private final JoiningRepository joiningRepository;
    private ClubService clubService;

    protected JoiningServiceImpl(JoiningRepository joiningRepository) {
        super(joiningRepository);
        this.joiningRepository = joiningRepository;
    }

    @Autowired
    public void setClubService(ClubService clubService) {
        this.clubService = clubService;
    }

    @Autowired
    public void setUserService(UserService userService) {
        this.userService = userService;
    }

    @NonNull
    @Override
    public Page<Joining> pageAllJoiningByClubId(Integer clubId, Pageable pageable) {
        if (clubId == null) {
            return Page.empty();
        }
        return joiningRepository.findAllByIdClubId(clubId, pageable);
    }

    @NonNull
    @Override
    public Page<Joining> pageAllJoiningByUserId(Integer userId, Pageable pageable) {
        if (userId == null) {
            return Page.empty();
        }
        return joiningRepository.findAllByIdUserId(userId, pageable);
    }

    @Override
    public List<Joining> listAllJoiningByUserId(Integer userId) {
        if (userId == null) {
            return Collections.emptyList();
        }
        return joiningRepository.findAllByIdUserId(userId);
    }

    @Override
    @NonNull
    public Optional<Joining> getJoiningById(@Nullable Integer userId, @Nullable Integer clubId) {
        if (userId == null || clubId == null) {
            return Optional.empty();
        }
        JoiningId id = new JoiningId(userId, clubId);
        return Optional.ofNullable(getByIdOfNullable(id));
    }

    @Override
    public boolean hasJoined(@NonNull Integer userId, @NonNull Integer clubId) {
        Assert.notNull(userId, "用户 ID 不能为 null");
        Assert.notNull(clubId, "社团 ID 不能为 null");

        JoiningId jid = new JoiningId(userId, clubId);
        return getByIdOfNullable(jid) == null;
    }

    @Override
    public void joinIfAbsent(@NonNull Integer userId, @NonNull Integer clubId, boolean admin) {
        Assert.notNull(userId, "用户 ID 不能为 null");
        Assert.notNull(clubId, "社团 ID 不能为 null");

        JoiningId jid = new JoiningId(userId, clubId);
        Joining joining = getByIdOfNullable(jid);
        if (joining == null) {
            Joining newMember = new Joining();
            newMember.setId(jid);
            newMember.setAdmin(admin);
            joiningRepository.save(newMember);
        }
    }

    @Override
    public void revokeAdmin(@NonNull Integer userId, @NonNull Integer clubId) {
        Assert.notNull(userId, "用户 ID 不能为 null");
        Assert.notNull(clubId, "社团 ID 不能为 null");

        JoiningId jid = new JoiningId(userId, clubId);
        Joining joining = getById(jid);
        if (joining.getAdmin()) {
            joining.setAdmin(false);
            update(joining);
        }
    }

    @Override
    @Transactional
    public void addMember(@NonNull NewMemberParam param) {
        ValidationUtils.validate(param, CreateCheck.class);  // 表单校验
        Optional<User> userOptional = userService.getByStudentNo(param.getStudentNo());
        Club targetClub = clubService.getById(param.getClubId());  // 确保社团存在
        if (userOptional.isPresent()) {
            JoiningId jid = new JoiningId(userOptional.get().getId(), param.getClubId());
            Joining joining = getByIdOfNullable(jid);
            if (joining != null) { // 已存在
                throw new BadRequestException("用户" + userOptional.get().getRealName() + "已为" + targetClub.getName() + "社团成员");
            }
        } else { // 用户不存在的情况，要首先新建用户
            User newUser = new User();
            newUser.setRole(param.getAdmin() ? UserRole.CLUB_ADMIN : UserRole.NORMAL);
            newUser.setRealName(param.getRealName());
            newUser.setStudentNo(param.getStudentNo());
        }
        Joining newJoining = new Joining();
        newJoining.setAdmin(param.getAdmin());
        if (!StringUtils.isEmpty(param.getPosition())) {
            newJoining.setPosition(param.getPosition());
        }
        joiningRepository.save(newJoining);
    }

    @Override
    @Transactional
    public void removeMember(@NonNull NewMemberParam param) {
        ValidationUtils.validate(param, UpdateCheck.class);  // 表单校验
        Optional<User> userOptional = userService.getByStudentNo(param.getStudentNo());
        clubService.getById(param.getClubId());  // 确保社团存在
        if (userOptional.isPresent()) {
            JoiningId jid = new JoiningId(userOptional.get().getId(), param.getClubId());
            getById(jid);  // 确保加入该社团
            if (userService.managerOf(SecurityContextHolder.getCurrentUser()
                    .orElseThrow(() -> new ForbiddenException("未登录")), userOptional.get())) {
                throw new ForbiddenException("权限不足，无法操作该用户");
            }
            joiningRepository.deleteById(jid);
            // TODO：删除社团活动室未来时段占用
        } else {
            throw new NotFoundException("用户不存在");
        }
    }

    @Override
    public void deleteByUserId(Integer userId) {
        joiningRepository.deleteByIdUserId(userId);
    }

    @Override
    public List<Joining> listAllJoiningByClubId(Integer clubId) {
        if (clubId == null) {
            return Collections.emptyList();
        }
        return joiningRepository.findAllByIdClubId(clubId);
    }

    @Override
    public void removeByIdClubId(Integer clubId) {
        joiningRepository.deleteByIdClubId(clubId);
    }
}
