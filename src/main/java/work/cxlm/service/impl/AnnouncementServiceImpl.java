package work.cxlm.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import work.cxlm.exception.ForbiddenException;
import work.cxlm.model.dto.AnnouncementDTO;
import work.cxlm.model.entity.Announcement;
import work.cxlm.model.entity.User;
import work.cxlm.model.params.AnnouncementParam;
import work.cxlm.repository.AnnouncementRepository;
import work.cxlm.security.context.SecurityContextHolder;
import work.cxlm.service.AnnouncementService;
import work.cxlm.service.NoticeService;
import work.cxlm.service.UserService;
import work.cxlm.service.base.AbstractCrudService;
import work.cxlm.utils.ServiceUtils;

import java.util.Arrays;
import java.util.List;

/**
 * created 2020/12/15 20:57
 *
 * @author Chiru
 */
@Service
@Slf4j
public class AnnouncementServiceImpl extends AbstractCrudService<Announcement, Integer> implements AnnouncementService {

    private final AnnouncementRepository repository;

    private NoticeService noticeService;
    private UserService userService;

    public AnnouncementServiceImpl(AnnouncementRepository repository) {
        super(repository);
        this.repository = repository;
    }

    @Autowired
    public void setUserService(UserService userService) {
        this.userService = userService;
    }

    @Autowired
    public void setNoticeService(NoticeService noticeService) {
        this.noticeService = noticeService;
    }

    @Override
    public List<AnnouncementDTO> getClubAnnouncements(@NonNull Integer clubId) {
        Assert.notNull(clubId, "社团 ID 不能为 null");

        User admin = SecurityContextHolder.ensureUser();

        List<Announcement> announcements;
        if (admin.getRole().isSystemAdmin()) {
            announcements = repository.findAllByClubIdIn(Arrays.asList(-1, clubId));
        } else {
            announcements = repository.findAllByClubId(clubId);
        }
        return ServiceUtils.convertList(announcements, announcement -> {
            AnnouncementDTO dto = new AnnouncementDTO().convertFrom(announcement);
            dto.fromUserData(userService.getById(announcement.getPublisherId()));
            return dto;
        });
    }

    private AnnouncementDTO saveAnnouncement(@NonNull AnnouncementParam param, boolean newEntity) {
        Assert.notNull(param, "AnnouncementParam 不能为 null");

        User admin = SecurityContextHolder.ensureUser();
        Integer clubId = param.getClubId();
        ensureAuthority(admin, clubId);

        Announcement nowAnnouncement = param.convertTo();
        if (newEntity) {
            nowAnnouncement.setPublisherId(admin.getId());
            nowAnnouncement = create(nowAnnouncement);
            noticeService.announce(nowAnnouncement);
        } else {
            Announcement oldAnnouncement = getById(param.getId());
            param.update(oldAnnouncement);
            nowAnnouncement = update(oldAnnouncement);
        }
        AnnouncementDTO dto = new AnnouncementDTO().convertFrom(nowAnnouncement);
        dto.fromUserData(admin);
        return dto;
    }

    @Override
    public AnnouncementDTO createBy(@NonNull AnnouncementParam param) {
        return saveAnnouncement(param, true);
    }

    @Override
    public AnnouncementDTO updateBy(@NonNull AnnouncementParam param) {
        return saveAnnouncement(param, false);
    }

    @Override
    public AnnouncementDTO deleteOne(@NonNull Integer annoId) {
        Assert.notNull(annoId, "公告 ID 不能为 null");

        // 权限验证
        User admin = SecurityContextHolder.ensureUser();
        Announcement toDelete = getById(annoId);
        Integer clubId = toDelete.getClubId();
        ensureAuthority(admin, clubId);

        remove(toDelete);
        AnnouncementDTO dto = new AnnouncementDTO().convertFrom(toDelete);
        dto.fromUserData(admin);
        return dto;
    }

    @Override
    public void deleteClubAllAnnouncements(@NonNull Integer clubId) {
        Assert.notNull(clubId, "社团 ID 不能为 null");
        if (clubId == -1) {
            log.error("危险：尝试删除全部系统公告，已阻止");
            return;
        }

        repository.deleteByClubId(clubId);
    }

    @Override
    public Page<AnnouncementDTO> pageClubAnnouncements(Integer clubId, Pageable pageable) {
        return ServiceUtils.convertPageElements(repository.findAllByClubId(clubId, pageable), pageable, anno -> {
            AnnouncementDTO dto = new AnnouncementDTO().convertFrom(anno);
            dto.fromUserData(userService.getById(anno.getPublisherId()));
            return dto;
        });
    }

    private void ensureAuthority(User admin, Integer clubId) {
        // 权限校验，注意：系统管理员无视权限
        boolean notSystemAdmin = !admin.getRole().isSystemAdmin();
        boolean notManagerOfClub = clubId == -1 || !userService.managerOfClub(admin, clubId);
        if (notSystemAdmin && notManagerOfClub) {
            throw new ForbiddenException("您的权限不足，无法操作公告信息");
        }
    }

    @Override
    public AnnouncementDTO getAnnouncementDtoById(Integer annoId) {
        Announcement src = getById(annoId);
        User publisher = userService.getById(src.getPublisherId());
        AnnouncementDTO dto = new AnnouncementDTO().convertFrom(src);
        dto.fromUserData(publisher);
        return dto;
    }
}
