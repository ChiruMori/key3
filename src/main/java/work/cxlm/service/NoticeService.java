package work.cxlm.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.lang.NonNull;
import work.cxlm.model.dto.NoticeDTO;
import work.cxlm.model.entity.Announcement;
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
     *
     * @param param 表单实例
     * @return NoticeDTO 并未附加 User 数据，只有 User ID
     */
    NoticeDTO createBy(@NotNull NoticeParam param);

    /**
     * 分页列出用户的全部通知
     *
     * @param pageable 分页参数
     * @return 通知的分页数据集
     */
    Page<NoticeDTO> pageAllNoticeOfUser(@NonNull Pageable pageable);

    /**
     * 发送通知并进行存储
     *
     * @param targetUser 接受者
     * @param publisher  发送者
     * @param content    内容
     * @param type       通知类型（泛型）
     */
    void notifyAndSave(@NonNull NoticeType type, String content, @NonNull User targetUser, User publisher);

    /**
     * 批量发送通知并进行存储
     *
     * @param notices 通知集合
     */
    void saveAndNotifyInBatch(@NonNull Collection<Notice> notices);

    /**
     * 通过公告实体发送通知
     *
     * @param nowAnnouncement 新公告
     */
    void announce(@NonNull Announcement nowAnnouncement);

    /**
     * 通过参数进行留言
     *
     * @param param 留言内容
     */
    void leaveNoteBy(@NonNull NoticeParam param);
}
