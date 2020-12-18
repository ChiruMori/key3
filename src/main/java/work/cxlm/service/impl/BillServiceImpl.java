package work.cxlm.service.impl;

import cn.hutool.core.lang.Assert;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import work.cxlm.event.LogEvent;
import work.cxlm.exception.ForbiddenException;
import work.cxlm.model.dto.BillDTO;
import work.cxlm.model.entity.Bill;
import work.cxlm.model.entity.Club;
import work.cxlm.model.entity.User;
import work.cxlm.model.params.BillParam;
import work.cxlm.model.params.LogParam;
import work.cxlm.model.vo.BillTableVO;
import work.cxlm.model.vo.BillVO;
import work.cxlm.repository.BillRepository;
import work.cxlm.security.context.SecurityContextHolder;
import work.cxlm.service.BillService;
import work.cxlm.service.ClubService;
import work.cxlm.service.UserService;
import work.cxlm.service.base.AbstractCrudService;
import work.cxlm.utils.ServiceUtils;

import java.util.List;

/**
 * created 2020/11/26 14:39
 *
 * @author Chiru
 */
@Service
public class BillServiceImpl extends AbstractCrudService<Bill, Integer> implements BillService {

    private final BillRepository billRepository;
    private final ApplicationEventPublisher eventPublisher;
    private final UserService userService;

    private ClubService clubService;

    protected BillServiceImpl(BillRepository repository,
                              UserService userService,
                              ApplicationEventPublisher eventPublisher) {
        super(repository);
        this.billRepository = repository;
        this.userService = userService;
        this.eventPublisher = eventPublisher;
    }

    @Autowired
    public void setClubService(ClubService clubService) {
        this.clubService = clubService;
    }

    @Override
    public Page<BillDTO> pageClubLatest(int top, Integer clubId, boolean showHead) {
        Assert.isTrue(top > 0, "每页条目必须大于 0");
        // 按创建时间降序排序，并取第一页
        PageRequest latestPageable = PageRequest.of(0, top, Sort.by(Sort.Direction.DESC, "createTime"));
        return billRepository.findAllByClubId(clubId, latestPageable).map(b -> wrapLogWithHeadAndWho(new BillDTO().convertFrom(b), showHead));
    }


    private BillDTO wrapLogWithHeadAndWho(BillDTO billDTO, boolean showHead) {
        if (!showHead) {
            return billDTO;
        }
        User targetUser = userService.getByIdOfNullable(billDTO.getAuthorId());
        billDTO.fromUserData(targetUser);
        return billDTO;
    }

    @Override
    public void removeByClubId(Integer clubId) {
        billRepository.deleteByClubId(clubId);
    }

    @Override
    public BillTableVO listClubAllBill(Integer clubId) {
        User admin = SecurityContextHolder.ensureUser();
        Club targetClub = clubService.getById(clubId);
        if (!userService.managerOfClub(admin, targetClub)) {
            throw new ForbiddenException("您的权限不足，禁止操作");
        }
        List<BillDTO> billDTOs = ServiceUtils.convertList(billRepository.findAllByClubId(clubId),
                bill -> wrapLogWithHeadAndWho(new BillDTO().convertFrom(bill), true));
        BillTableVO tableVO = new BillTableVO();
        tableVO.setBills(billDTOs);
        tableVO.setClubAssets(targetClub.getAssets());
        return tableVO;
    }

    @Override
    @Transactional
    public BillVO saveBillBy(@NonNull BillParam param) {
        Assert.notNull(param, "BillParam 不能为 null");
        // 验证
        Club targetClub = clubService.getById(param.getClubId());
        User admin = SecurityContextHolder.ensureUser();
        if (!userService.managerOfClub(admin, targetClub)) {
            throw new ForbiddenException("您的权限不足，无法操作");
        }
        // 存储
        Bill newBill = param.convertTo();
        newBill.setAuthorId(admin.getId());
        if (newBill.getId() != null) {  // 更新模式，需要删除原账单导致的经费变化
            Bill oldBill = getById(newBill.getId());
            targetClub.setAssets(targetClub.getAssets().subtract(oldBill.getCost()));
            param.update(oldBill);
            newBill = oldBill;
        }
        targetClub.setAssets(targetClub.getAssets().add(newBill.getCost()));
        update(newBill);
        eventPublisher.publishEvent(new LogEvent(this, new LogParam(admin.getId(), targetClub.getId(),
                "更新了社团收支：" + newBill.getInfo())));
        clubService.update(targetClub);
        // 响应
        BillVO billVO = new BillVO().convertFrom(newBill);
        billVO.setWho(admin.getRealName());
        billVO.setShowHead(admin.getHead());
        billVO.setClubAssets(targetClub.getAssets());
        return billVO;
    }

    @Override
    @Transactional
    public BillVO deleteBill(Integer billId) {
        // 验证
        User admin = SecurityContextHolder.ensureUser();
        Bill targetBill = getById(billId);
        Club targetClub = clubService.getById(targetBill.getClubId());
        if (!userService.managerOfClub(admin, targetClub)) {
            throw new ForbiddenException("您的权限不足，无法操作");
        }
        // 移除、更新社团经费
        targetClub.setAssets(targetClub.getAssets().subtract(targetBill.getCost()));
        clubService.update(targetClub);
        eventPublisher.publishEvent(new LogEvent(this, new LogParam(admin.getId(), targetClub.getId(),
                "删除了社团收支项：" + targetBill.getInfo())));
        // 响应
        BillVO billVO = new BillVO().convertFrom(targetBill);
        billVO.setWho(admin.getRealName());
        billVO.setShowHead(admin.getHead());
        billVO.setClubAssets(targetClub.getAssets());
        return billVO;
    }
}
