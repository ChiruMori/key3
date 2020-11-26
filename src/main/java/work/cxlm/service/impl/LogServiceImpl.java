package work.cxlm.service.impl;

import cn.hutool.core.lang.Assert;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import work.cxlm.model.dto.LogDTO;
import work.cxlm.model.entity.Log;
import work.cxlm.model.entity.User;
import work.cxlm.model.support.QfzsConst;
import work.cxlm.repository.LogRepository;
import work.cxlm.service.LogService;
import work.cxlm.service.UserService;
import work.cxlm.service.base.AbstractCrudService;

/**
 * created 2020/10/29 15:30
 *
 * @author ryanwang
 * @author cxlm
 */
@Service
public class LogServiceImpl extends AbstractCrudService<Log, Long> implements LogService {

    private final LogRepository logRepository;
    private final UserService userService;

    public LogServiceImpl(LogRepository logRepository,
                          UserService userService) {
        super(logRepository);
        this.logRepository = logRepository;
        this.userService = userService;
    }

    @Override
    public Page<LogDTO> pageLatest(int top, boolean showHead) {
        Assert.isTrue(top > 0, "每页条目必须大于 0");

        // 按创建时间降序排序，并取第一页
        PageRequest latestPageable = PageRequest.of(0, top, Sort.by(Sort.Direction.DESC, "createTime"));
        // 查询出指定范围的日志，并将每个实体转化为 DTO
        return listAll(latestPageable).map(log -> wrapLogWithHeadAndWho(new LogDTO().convertFrom(log), showHead));
    }

    @Override
    public Page<LogDTO> pageClubLatest(int top, Integer clubId, boolean showHead) {
        if (clubId == -1) {
            return pageLatest(top, showHead);
        }
        Assert.isTrue(top > 0, "每页条目必须大于 0");
        // 按创建时间降序排序，并取第一页
        PageRequest latestPageable = PageRequest.of(0, top, Sort.by(Sort.Direction.DESC, "createTime"));
        return logRepository.findAllByLogKey(clubId, latestPageable).map(log -> wrapLogWithHeadAndWho(new LogDTO().convertFrom(log), showHead));
    }

    private LogDTO wrapLogWithHeadAndWho(LogDTO logDTO, boolean showHead) {
        if (!showHead) {
            return logDTO;
        }
        if (logDTO.getLogKey() == null || logDTO.getLogKey() == -1) {
            logDTO.setShowHead(QfzsConst.SYSTEM_HEAD);
            logDTO.setWho("系统");
            return logDTO;
        }
        User targetUser = userService.getByIdOfNullable(logDTO.getLogKey());
        if (targetUser == null) {
            logDTO.setWho("不明人士");
            logDTO.setShowHead(QfzsConst.SYSTEM_HEAD);
            return logDTO;
        }
        logDTO.setWho(targetUser.getRealName());
        logDTO.setShowHead(targetUser.getHead());
        return logDTO;
    }
}
