package work.cxlm.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import work.cxlm.model.dto.LogDTO;
import work.cxlm.model.entity.Log;
import work.cxlm.service.base.CrudService;

import java.util.Date;
import java.util.List;

/**
 * created 2020/10/29 15:16
 *
 * @author cxlm
 */
public interface LogService extends CrudService<Log, Long> {

    /**
     * 列出某社团可以查看的最近日志
     *
     * @param top    条目数
     * @param clubId 社团 ID，当前角色为系统管理员时则不顾一切分页列出全部日志的前 top 项
     * @return 社团日志的分页数据集
     */
    Page<LogDTO> pageClubLatest(int top, Integer clubId);

    /**
     * 列出某社团的全部日志，如果当前角色是系统管理员则不顾一切列出全部日志。
     * 因使用后端分页将无法更便捷地使用排序，但数据量大的时候，可能仍需分页优化
     *
     * @param clubId 指定社团的 ID
     * @return 社团的全部日志列表
     */
    List<LogDTO> listAllByClubId(Integer clubId);

    /**
     * 清除某个时间点以前的全部日志
     *
     * @param timeTo 指定的时间点
     * @return 删除的记录条数
     */
    int cleanAllBeforeDate(Date timeTo);
}
