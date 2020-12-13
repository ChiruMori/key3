package work.cxlm.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import work.cxlm.mail.MailContentHelper;
import work.cxlm.mail.MailService;
import work.cxlm.model.dto.NoticeDTO;
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

        Map<Integer, User> userMap = userService.getAllUserMap();
        Page<Notice> notices = noticeRepository.findAllByTargetUserId(nowUser.getId(), pageable);
        return ServiceUtils.convertPageElements(notices, pageable, notice -> {
            NoticeDTO res = new NoticeDTO().convertFrom(notice);
            res.fromUserData(userMap.get(notice.getSrcUserId()));
            return res;
        });
    }

    @Override
    public void notifyAndSave(@NonNull NoticeType type, String content, @NonNull User targetUser, User publisher) {
        Assert.notNull(type, "必须指定消息类型");
        Assert.notNull(targetUser, "目标用户不能为 null");

        Integer publisherId = null;
        if (publisher != null) {
            publisherId = publisher.getId();
        }
        Notice newNotice = new Notice(type, content, publisherId, targetUser.getId());
        create(newNotice);
        // 如果目标用户不接受邮件消息
        if (!targetUser.getReceiveMsg()) {
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
                .setNotice(newNotice)
                .setTargetUserName(targetUser.getRealName())
                .setPublisherName(srcUserName)
                .setMiniCodeUrl(optionService.getByPropertyOrDefault(PrimaryProperties.MINI_CODE_URL, String.class))
                .build();
        // 异步发送消息，在批量操作是不存在问题
        mailService.sendTemplateMail(targetUser.getEmail(), newNotice.getTitle(), data, "mail/notice");
    }

    @Override
    public void saveAndNotifyInBatch(@NonNull Collection<Notice> notices) {
        Assert.notNull(notices, "通知列表不能为 null");
        Map<Integer, User> allUserMap = userService.getAllUserMap();
        notices.forEach(notice -> {
                    if (notice.getTargetUserId() == -1) {
                        log.error("不能向系统用户（id=-1）发送消息！");
                    }
                    User targetUser = allUserMap.get(notice.getTargetUserId());
                    if (targetUser == null) {
                        log.error("找不到用户：[{}]，无法发送通知: [{}]", notice.getTargetUserId(), notice);
                        return;
                    }
                    notifyAndSave(notice.getType(), notice.getContent(),
                            targetUser, allUserMap.get(notice.getSrcUserId()));
                }
        );
    }
}
