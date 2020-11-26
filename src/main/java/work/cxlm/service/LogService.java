package work.cxlm.service;

import org.springframework.data.domain.Page;
import work.cxlm.model.dto.LogDTO;
import work.cxlm.model.entity.Log;
import work.cxlm.service.base.CrudService;

/**
 * created 2020/10/29 15:16
 *
 * @author cxlm
 */
public interface LogService extends CrudService<Log, Long> {

    /**
     * 列出最近的日志
     * @param top 条目数
     * @param showHead 是否需要补充头像、用户信息
     */
    Page<LogDTO> pageLatest(int top, boolean showHead);

    /**
     * 列出某社团可以查看的最近日志
     * @param top 条目数
     * @param clubId 社团 ID，可以传递 -1 表示不限社团
     * @param showHead 是否需要补充头像、用户信息
     */
    Page<LogDTO> pageClubLatest(int top, Integer clubId, boolean showHead);
}
