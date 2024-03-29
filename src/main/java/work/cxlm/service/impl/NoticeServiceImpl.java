package work.cxlm.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import work.cxlm.mail.MailContentHelper;
import work.cxlm.service.MailService;
import work.cxlm.model.dto.NoticeDTO;
import work.cxlm.model.entity.Announcement;
import work.cxlm.model.entity.Notice;
import work.cxlm.model.entity.User;
import work.cxlm.model.enums.NoticeType;
import work.cxlm.model.params.NoticeParam;
import work.cxlm.model.properties.PrimaryProperties;
import work.cxlm.repository.NoticeRepository;
import work.cxlm.security.context.SecurityContextHolder;
import work.cxlm.service.NoticeService;
import work.cxlm.service.OptionService;
import work.cxlm.service.UserService;
import work.cxlm.service.base.AbstractCrudService;
import work.cxlm.utils.ServiceUtils;

import javax.validation.constraints.NotNull;
import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * created 2020/12/10 14:59
 *
 * @author Chiru
 */
@Service
@Slf4j
public class NoticeServiceImpl extends AbstractCrudService<Notice, Long> implements NoticeService {

    private final NoticeRepository noticeRepository;

    private UserService userService;
    private OptionService optionService;
    private MailService mailService;

    protected NoticeServiceImpl(NoticeRepository repository,
                                OptionService optionService,
                                MailService mailService) {
        super(repository);
        noticeRepository = repository;
        this.optionService = optionService;
        this.mailService = mailService;
    }

    @Autowired
    public void setUserService(UserService userService) {
        this.userService = userService;
    }

    @Autowired
    public void setMailService(MailService mailService) {
        this.mailService = mailService;
    }

    @Autowired
    public void setOptionService(OptionService optionService) {
        this.optionService = optionService;
    }

    @Override
    public NoticeDTO createBy(@NotNull NoticeParam param) {
        Assert.notNull(param, "NoticeParam 实例不能为 null");

        Notice newNotice = param.convertTo();
        newNotice = create(newNotice);
        return new NoticeDTO().convertFrom(newNotice);
    }

    @Override
    public Page<NoticeDTO> pageAllNoticeOfUser(@NonNull Pageable pageable) {
        Assert.notNull(pageable, "Pageable 对象不能为 null");
        User nowUser = SecurityContextHolder.ensureUser();

        Page<Notice> notices = noticeRepository.findAllByTargetUserId(nowUser.getId(), pageable);
        return ServiceUtils.convertPageElements(notices, pageable, notice -> {
            NoticeDTO res = new NoticeDTO().convertFrom(notice);
            Integer targetUserId = notice.getSrcId();
            if (notice.getType().anncounceType()) {
                targetUserId = -1;
            }
            res.fromUserData(userService.getById(targetUserId));
            return res;
        });
    }

    @Override
    public void notifyByMail(@NonNull NoticeType type, String content, @NonNull User targetUser, User publisher) {
        Assert.notNull(type, "必须指定消息类型");
        Assert.notNull(targetUser, "目标用户不能为 null");
        Assert.isTrue(!type.anncounceType(), "公告通知类型请调用 announceAndSave 方法");

        Integer publisherId = null;
        if (publisher != null) {
            publisherId = publisher.getId();
        }
        Notice newNotice = new Notice(type, content, publisherId, targetUser.getId());
        // create(newNotice); 不再进行冗余存储
        mailTo(publisher, targetUser, newNotice);
    }

    public void announceAndSave(@NonNull NoticeType type, @NonNull User targetUser,
                                User publisher, Announcement announcement) {
        Assert.notNull(type, "必须指定消息类型");
        Assert.notNull(targetUser, "目标用户不能为 null");
        Assert.isTrue(type.anncounceType(), "非公告通知类型请调用 notifyAndSave 方法");

        // 系统用户忽略通知
        if (targetUser.getId() == -1) {
            return;
        }
        Notice newNotice = new Notice(type, "您有新的公告：【" + announcement.getTitle() + "】",
                announcement.getId(), targetUser.getId());
        create(newNotice);
        mailTo(publisher, targetUser, newNotice);
    }

    private void mailTo(User publisher, User receiver, Notice notice) {
        // 如果目标用户不接受邮件消息
        if (receiver.getReceiveMsg() == null || !receiver.getReceiveMsg()) {
            return;
        }
        // 构建邮件消息并发送
        String srcUserName;
        if (publisher == null) {
            srcUserName = "系统";
        } else {
            srcUserName = publisher.getRealName();
        }
        Map<String, Object> data = new MailContentHelper()
                .setNotice(notice)
                .setTargetUserName(receiver.getRealName())
                .setPublisherName(srcUserName)
                .setMiniCodeUrl(optionService.getByPropertyOrDefault(PrimaryProperties.MINI_CODE_URL, String.class))
                .build();
        // 异步发送消息，在批量操作是不存在问题
        mailService.sendTemplateMail(receiver.getEmail(), notice.getTitle(), data, "mail/notice.ftl");
    }

    @Override
    public void notifyByMailInBatch(@NonNull Collection<Notice> notices) {
        Assert.notNull(notices, "通知列表不能为 null");
        notices.forEach(notice -> {
                    if (notice.getTargetUserId() == -1) {
                        log.error("不能向系统用户（id=-1）发送消息！");
                    }
                    User targetUser = userService.getById(notice.getTargetUserId());
                    Integer targetUserId = notice.getSrcId();
                    if (notice.getType().anncounceType()) {
                        targetUserId = -1;
                    }
                    notifyByMail(notice.getType(), notice.getContent(),
                            targetUser, userService.getById(targetUserId));
                }
        );
    }

    @Override
    public void announce(@NonNull Announcement nowAnnouncement) {
        Assert.notNull(nowAnnouncement, "公告对象不能为 null");

        User admin = SecurityContextHolder.ensureUser();
        List<User> receivers;
        NoticeType noticeType;
        if (nowAnnouncement.getClubId() == -1) {
            noticeType = NoticeType.ADMIN_ANNOUNCEMENT;
            receivers = userService.listAll();
        } else {
            noticeType = NoticeType.CLUB_ANNOUNCEMENT;
            receivers = userService.getClubUsers(nowAnnouncement.getClubId());
        }
        receivers.forEach(user -> announceAndSave(noticeType, user, admin, nowAnnouncement));
    }

    @Override
    public void leaveNoteBy(@NonNull NoticeParam param) {
        Assert.notNull(param, "NoticeParam 不能为 null");

        User publisher = SecurityContextHolder.ensureUser();
        User receiver = userService.getById(param.getTargetUserId());
        notifyByMail(NoticeType.NEW_MESSAGE, param.getContent(), receiver, publisher);
    }
}
