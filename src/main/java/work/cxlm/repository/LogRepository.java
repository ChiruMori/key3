package work.cxlm.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import work.cxlm.model.entity.Log;

import java.util.List;

/**
 * created 2020/10/29 15:31
 *
 * @author cxlm
 */
public interface LogRepository extends BaseRepository<Log, Long> {

    Page<Log> findAllByGroupId(Integer logKey, Pageable pageable);

    List<Log> findAllByGroupId(Integer groupId);
}
