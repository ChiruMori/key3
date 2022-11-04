package work.cxlm.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import work.cxlm.model.entity.Log;

import javax.transaction.Transactional;
import java.util.Date;
import java.util.List;

/**
 * created 2020/10/29 15:31
 *
 * @author cxlm
 */
public interface LogRepository extends BaseRepository<Log, Long> {

    /**
     * 分页查找某分组的日志
     *
     * @param logKey   分组 ID
     * @param pageable 分页参数
     * @return 某分组日志的分页数据集
     */
    Page<Log> findAllByGroupId(Integer logKey, Pageable pageable);

    /**
     * 查找某社团的全部日志
     *
     * @param groupId  社团 ID
     * @return 某社团日志的分页数据集
     */
    List<Log> findAllByGroupId(Integer groupId);

    /**
     * 删除某个时间点以前的全部记录
     *
     * @param timeTo 截至时间
     * @return 删除记录条数
     */
    @Transactional(rollbackOn = Exception.class)
    int deleteByCreateTimeBefore(Date timeTo);
}
