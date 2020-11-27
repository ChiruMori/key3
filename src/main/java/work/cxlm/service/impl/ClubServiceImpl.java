package work.cxlm.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;
import work.cxlm.event.logger.LogEvent;
import work.cxlm.exception.ForbiddenException;
import work.cxlm.model.dto.ClubDTO;
import work.cxlm.model.entity.Bill;
import work.cxlm.model.entity.Club;
import work.cxlm.model.entity.User;
import work.cxlm.model.enums.LogType;
import work.cxlm.model.params.ClubParam;
import work.cxlm.model.support.CreateCheck;
import work.cxlm.model.support.UpdateCheck;
import work.cxlm.repository.ClubRepository;
import work.cxlm.security.context.SecurityContextHolder;
import work.cxlm.service.*;
import work.cxlm.service.base.AbstractCrudService;
import work.cxlm.utils.ValidationUtils;

import java.math.BigDecimal;

/**
 * created 2020/11/21 15:24
 *
 * @author Chiru
 */
@Service
@Slf4j
public class ClubServiceImpl extends AbstractCrudService<Club, Integer> implements ClubService {

    private UserService userService;
    private JoiningService joiningService;
    private BillService billService;
    private final ApplicationEventPublisher eventPublisher;

    public ClubServiceImpl(ClubRepository repository,
                           ApplicationEventPublisher eventPublisher) {
        super(repository);
        this.eventPublisher = eventPublisher;
    }

    @Autowired
    public void setUserService(UserService userService) {
        this.userService = userService;
    }

    @Autowired
    public void setJoiningService(JoiningService joiningService) {
        this.joiningService = joiningService;
    }

    @Autowired
    public void setBillService(BillService billService) {
        this.billService = billService;
    }

    @Override
    public ClubDTO getManagedClubInfo(Integer clubId) {
        Assert.notNull(clubId, "社团 ID 不能为 null");
        User admin = SecurityContextHolder.ensureUser();
        Club targetClub = getById(clubId);
        if (!userService.managerOfClub(admin, targetClub)) {
            throw new ForbiddenException("权限不足，禁止查看");
        }
        return new ClubDTO().convertFrom(targetClub);
    }

    @Override
    public ClubDTO newClubByParam(ClubParam clubParam) {
        Assert.notNull(clubParam, "ClubParam 不能为 null");
        ValidationUtils.validate(clubParam, CreateCheck.class);

        User admin = SecurityContextHolder.ensureUser();
        if (!admin.getRole().isSystemAdmin()) {
            throw new ForbiddenException("只有系统管理员可以执行本操作");
        }
        Club newClub = create(clubParam.convertTo());
        eventPublisher.publishEvent(new LogEvent(this, admin.getId(), LogType.CLUB_EVENT,
                "创建了新的社团：" + newClub.getName()));
        return new ClubDTO().convertFrom(newClub);
    }

    @Override
    @Transactional
    public ClubDTO updateByParam(ClubParam clubParam) {
        Assert.notNull(clubParam, "ClubParam 不能为 null");
        ValidationUtils.validate(clubParam, UpdateCheck.class);

        User admin = SecurityContextHolder.ensureUser();
        if (admin.getRole().isNormalRole()) {
            throw new ForbiddenException("权限不足，禁止操作");
        }

        Club targetClub = getById(clubParam.getClubId());
        if (!userService.managerOfClub(admin, targetClub)) {
            throw new ForbiddenException("权限不足，禁止操作");
        }
        if (!clubParam.getAssets().equals(targetClub.getAssets())) {
            BigDecimal assetChange = clubParam.getAssets().subtract(targetClub.getAssets());
            Bill newBill = new Bill();
            newBill.setAuthorId(admin.getId());
            newBill.setClubId(targetClub.getId());
            newBill.setCost(assetChange);
            newBill.setInfo("管理员" + admin.getRealName() + "手动调整");
            billService.create(newBill);
        }
        clubParam.update(targetClub);
        update(targetClub);
        return new ClubDTO().convertFrom(targetClub);
    }

    @Override
    public void deleteClub(Integer clubId) {
        Assert.notNull(clubId, "clubId 不能为 null");

        User admin = SecurityContextHolder.ensureUser();
        if (!admin.getRole().isSystemAdmin()) {
            throw new ForbiddenException("权限不足，禁止操作");
        }
        joiningService.removeByIdClubId(clubId);  // 删除用户加入社团的信息
        billService.removeByClubId(clubId);  // 删除社团财务信息
        // TODO 删除公告信息
        // TODO 删除活动室归属信息
        removeById(clubId);  // 删除社团
        eventPublisher.publishEvent(new LogEvent(this, admin.getId(), LogType.CLUB_EVENT,
                "删除了社团：" + clubId));
    }
}
