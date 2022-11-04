package work.cxlm.service.impl;

import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.exception.ExcelDataConvertException;
import com.alibaba.excel.read.listener.ReadListener;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;
import work.cxlm.event.JoiningOrBelongUpdatedEvent;
import work.cxlm.event.LogEvent;
import work.cxlm.exception.AbstractKey3Exception;
import work.cxlm.exception.DataConflictException;
import work.cxlm.exception.ForbiddenException;
import work.cxlm.exception.NotFoundException;
import work.cxlm.lock.CacheLock;
import work.cxlm.model.dto.JoiningDTO;
import work.cxlm.model.entity.Club;
import work.cxlm.model.entity.Joining;
import work.cxlm.model.entity.User;
import work.cxlm.model.entity.id.JoiningId;
import work.cxlm.model.enums.UserRole;
import work.cxlm.model.params.JoiningParam;
import work.cxlm.model.params.LogParam;
import work.cxlm.model.support.CreateCheck;
import work.cxlm.model.support.UpdateCheck;
import work.cxlm.repository.JoiningRepository;
import work.cxlm.security.context.SecurityContextHolder;
import work.cxlm.service.ClubService;
import work.cxlm.service.JoiningService;
import work.cxlm.service.TimeService;
import work.cxlm.service.UserService;
import work.cxlm.service.base.AbstractModifyNotifyCrudService;
import work.cxlm.utils.ServiceUtils;
import work.cxlm.utils.ValidationUtils;

import javax.validation.ValidationException;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.locks.ReentrantLock;

/**
 * created 2020/11/18 13:13
 *
 * @author Chiru
 */
@Service
@Slf4j
public class JoiningServiceImpl extends AbstractModifyNotifyCrudService<Joining, JoiningId> implements JoiningService {

    private UserService userService;
    private ClubService clubService;
    private TimeService timeService;

    private final JoiningRepository joiningRepository;
    private final ApplicationEventPublisher eventPublisher;

    protected JoiningServiceImpl(JoiningRepository joiningRepository,
                                 ApplicationEventPublisher eventPublisher) {
        super(joiningRepository, Joining::getId);
        this.joiningRepository = joiningRepository;
        this.eventPublisher = eventPublisher;
    }

    @Autowired
    public void setClubService(ClubService clubService) {
        this.clubService = clubService;
    }

    @Autowired
    public void setUserService(UserService userService) {
        this.userService = userService;
    }

    @Autowired
    public void setTimeService(TimeService timeService) {
        this.timeService = timeService;
    }

    //*************** Override ************************

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
    @Transactional(rollbackFor = Exception.class)
    public JoiningDTO newJoiningBy(@NonNull JoiningParam param) {
        User admin = SecurityContextHolder.ensureUser();
        // 表单校验
        ValidationUtils.validate(param, CreateCheck.class);
        Optional<User> userOptional = userService.getByStudentNo(param.getStudentNo());
        User targetUser;
        // 确保社团存在
        Club targetClub = clubService.getById(param.getClubId());
        // 用户已存在时
        if (userOptional.isPresent()) {
            JoiningId jid = new JoiningId(userOptional.get().getId(), param.getClubId());
            Joining joining = getByIdOfNullable(jid);
            // 该用户已经加入社团，抛
            if (joining != null) {
                throw new DataConflictException("用户" + userOptional.get().getRealName() +
                        "已为" + targetClub.getName() + "社团成员");
            }
            targetUser = userOptional.get();
        } else {
            // 用户不存在的情况，要首先新建用户
            if (StringUtils.isEmpty(param.getRealName())) {
                throw new ValidationException("新建用户时必须同时指定用户姓名");
            }
            targetUser = new User();
            targetUser.setRole(param.getAdmin() ? UserRole.CLUB_ADMIN : UserRole.NORMAL);
            targetUser.setRealName(param.getRealName());
            targetUser.setStudentNo(param.getStudentNo());
            // 存储
            targetUser = userService.create(targetUser);
        }
        // 新建用户与社团的联系
        Joining newJoining = param.convertTo();
        JoiningId joiningId = new JoiningId(targetUser.getId(), targetClub.getId());
        newJoining.setId(joiningId);
        newJoining = joiningRepository.save(newJoining);
        eventPublisher.publishEvent(new LogEvent(this, new LogParam(admin.getId(), targetClub.getId(),
                "增加了社团成员：" + targetUser.getRealName())));
        // 构造返回值
        return buildResult(newJoining, targetUser);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public JoiningDTO removeMember(@NonNull Integer clubId, @NonNull Long studentNo) {
        Assert.notNull(clubId, "社团 ID 不能为 null");
        Assert.notNull(studentNo, "学号不能为 null");

        Optional<User> userOptional = userService.getByStudentNo(studentNo);
        // 确保社团存在
        Club targetClub = clubService.getById(clubId);
        User admin = SecurityContextHolder.ensureUser();
        if (userOptional.isPresent()) {
            User targetUser = userOptional.get();
            JoiningId jid = new JoiningId(targetUser.getId(), clubId);
            // 确保加入该社团
            Joining toDelete = getById(jid);
            if (!userService.managerOf(admin, targetUser)) {
                throw new ForbiddenException("权限不足，无法操作该用户");
            }
            timeService.deleteUserFutureTime(targetUser, targetClub);
            joiningRepository.deleteById(jid);
            eventPublisher.publishEvent(new LogEvent(this, new LogParam(targetUser.getId(), targetClub.getId(),
                    "删除了社团成员：" + targetUser.getRealName())));
            // 构造返回值
            return buildResult(toDelete, targetUser);
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

    @Override
    public List<JoiningDTO> listAllJoiningDtosByClubId(Integer clubId) {
        return ServiceUtils.convertList(listAllJoiningByClubId(clubId), joining -> {
            User targetUser = userService.getById(joining.getId().getUserId());
            // 构造返回值
            return buildResult(joining, targetUser);
        });
    }

    @Override
    @NonNull
    public Joining getByIds(@NonNull Integer userId, @NonNull Integer clubId) {
        Assert.notNull(userId, "用户 ID 不能为 Null");
        Assert.notNull(clubId, "社团 ID 不能为 null");

        JoiningId joiningId = new JoiningId(userId, clubId);
        return getById(joiningId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public JoiningDTO updateJoiningBy(JoiningParam param) {
        ValidationUtils.validate(param, UpdateCheck.class);
        User targetUser = userService.getByStudentNo(param.getStudentNo()).orElseThrow(
                () -> new NotFoundException("学号不存在，您可以通过用户基本信息维护修改学号"));
        // 查询并存储
        Joining targetJoining = getByIds(targetUser.getId(), param.getClubId());
        param.update(targetJoining);
        targetJoining = update(targetJoining);
        // 如果用户姓名发生了改变需要在这里进行更新
        if (!Objects.equals(param.getRealName(), targetUser.getRealName())) {
            targetUser.setRealName(param.getRealName());
            userService.update(targetUser);
        }
        // 返回值构建
        return buildResult(targetJoining, targetUser);
    }

    @Override
    public boolean adminOfAny(@NonNull User targetUser) {
        Assert.notNull(targetUser, "目标用户不能为 null");
        List<Joining> userJoining = joiningRepository.findAllByIdUserId(targetUser.getId());
        return userJoining.stream().anyMatch(Joining::getAdmin);
    }

    @Override
    public List<Joining> listAllJoiningByUserIdIn(@NonNull List<Integer> userIds) {
        Assert.notNull(userIds, "userIds 不能为 null");
        if (CollectionUtils.isEmpty(userIds)) {
            return Collections.emptyList();
        }
        return joiningRepository.findAllByIdUserIdIn(userIds);
    }

    private JoiningDTO buildResult(Joining joining, User targetUser) {
        JoiningDTO res = new JoiningDTO().convertFrom(joining);
        res.setHead(targetUser.getHead());
        res.setRealName(targetUser.getRealName());
        res.setStudentNo(targetUser.getStudentNo());
        return res;
    }

    @Override
    protected void afterDeleted(@NonNull JoiningId joiningId) {
        eventPublisher.publishEvent(new JoiningOrBelongUpdatedEvent(this));
    }

    @Override
    protected void afterModified(@NonNull Joining joining) {
        eventPublisher.publishEvent(new JoiningOrBelongUpdatedEvent(this));
    }

    @Override
    protected void afterDeletedBatch(@Nullable Collection<JoiningId> joiningIds) {
        eventPublisher.publishEvent(new JoiningOrBelongUpdatedEvent(this));
    }

    @Override
    protected void afterModifiedBatch(@NonNull Collection<Joining> joinings) {
        eventPublisher.publishEvent(new JoiningOrBelongUpdatedEvent(this));
    }

    @Override
    @CacheLock(value = "file_import", msg = "其他文件正在导入，请稍后重试。。。")
    public List<String> importJoiningByFile(MultipartFile file, @NonNull Integer clubId) {
        try {
            List<String> resultList = new ArrayList<>();
            EasyExcel.read(file.getInputStream(), JoiningParam.class, new ReadListener<JoiningParam>() {
                @Override
                public void invoke(JoiningParam data, AnalysisContext context) {
                    data.setClubId(clubId);
                    data.setTotal(0);
                    // 事务可能失效
                    newJoiningBy(data);
                }

                @Override
                public void doAfterAllAnalysed(AnalysisContext context) {
                    log.info("Excel 解析完成");
                }

                @Override
                public void onException(Exception exception, AnalysisContext context) {
                    String errMsg;
                    if (exception instanceof ExcelDataConvertException) {
                        ExcelDataConvertException convertException = (ExcelDataConvertException) exception;
                        errMsg = String.format("第%d行，第%d列数据解析异常，原因：%s", convertException.getRowIndex(), convertException.getColumnIndex(), convertException.getMessage());
                    } else {
                        errMsg = String.format("第%d行数据导入失败，%s", context.readRowHolder().getRowIndex(), exception.getMessage());
                    }
                    log.error(errMsg);
                    resultList.add(errMsg);
                }
            }).sheet().doRead();
            return resultList;
        } catch (IOException e) {
            log.error("读取文件出错", e);
            return List.of("文件读取出错：" + e.getMessage());
        }
    }
}
