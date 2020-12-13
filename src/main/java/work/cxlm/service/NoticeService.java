package work.cxlm.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.lang.NonNull;
import work.cxlm.model.dto.NoticeDTO;
import work.cxlm.model.entity.Notice;
import work.cxlm.model.entity.User;
import work.cxlm.model.enums.NoticeType;
import work.cxlm.model.params.NoticeParam;
import work.cxlm.service.base.CrudService;

import javax.validation.constraints.NotNull;
import java.util.Collection;
import java.util.LinkedList;

/**
 * created 2020/12/10 14:59
 *
 * @author Chiru
 */
public interface NoticeService extends CrudService<Notice, Long> {

    /**
     * 通过参数创建一个 Notice 实例并保存
     * @return NoticeDTO 并未附加 User 数据，只有 User ID
     */
    NoticeDTO createBy(@NotNull NoticeParam param);

    /**
     * 分页列出用户的全部通知
     */
    Page<NoticeDTO> pageAllNoticeOfUser(@NonNull Pageable pageable);

    /**
     * 发送通知并进行存储
     */
    void notifyAndSave(@NonNull NoticeType type, String content, @NonNull User targetUser, User publisher);

    /**
     * 批量发送通知并进行存储
     * @param notices 通知集合
     */
    void saveAndNotifyInBatch(@NonNull Collection<Notice> notices);
}
