package work.cxlm.service.impl;

import cn.hutool.core.lang.Assert;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import work.cxlm.model.dto.BillDTO;
import work.cxlm.model.dto.LogDTO;
import work.cxlm.model.entity.Bill;
import work.cxlm.model.entity.User;
import work.cxlm.model.support.QfzsConst;
import work.cxlm.repository.BillRepository;
import work.cxlm.service.BillService;
import work.cxlm.service.UserService;
import work.cxlm.service.base.AbstractCrudService;

/**
 * created 2020/11/26 14:39
 *
 * @author Chiru
 */
@Service
public class BillServiceImpl extends AbstractCrudService<Bill, Integer> implements BillService {

    private final BillRepository billRepository;
    private final UserService userService;

    protected BillServiceImpl(BillRepository repository,
                              UserService userService) {
        super(repository);
        this.billRepository = repository;
        this.userService = userService;
    }

    @Override
    public Page<BillDTO> pageClubLatest(int top, Integer clubId, boolean showHead) {
        Assert.isTrue(top > 0, "每页条目必须大于 0");
        // 按创建时间降序排序，并取第一页
        PageRequest latestPageable = PageRequest.of(0, top, Sort.by(Sort.Direction.DESC, "createTime"));
        return billRepository.findAllyByClubId(clubId, latestPageable).map(b -> wrapLogWithHeadAndWho(new BillDTO().convertFrom(b), showHead));
    }


    private BillDTO wrapLogWithHeadAndWho(BillDTO billDTO, boolean showHead) {
        if (!showHead) {
            return billDTO;
        }
        User targetUser = userService.getByIdOfNullable(billDTO.getAuthorId());
        if (targetUser == null) {
            billDTO.setWho("不明人士");
            billDTO.setShowHead(QfzsConst.SYSTEM_HEAD);
            return billDTO;
        }
        billDTO.setWho(targetUser.getRealName());
        billDTO.setShowHead(targetUser.getHead());
        return billDTO;
    }

    @Override
    public void removeByClubId(Integer clubId) {
        billRepository.deleteByClubId(clubId);
    }
}
