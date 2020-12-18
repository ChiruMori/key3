package work.cxlm.service;

import org.springframework.lang.NonNull;
import work.cxlm.model.dto.AnnouncementDTO;
import work.cxlm.model.entity.Announcement;
import work.cxlm.model.params.AnnouncementParam;
import work.cxlm.service.base.CrudService;

import java.util.List;

/**
 * created 2020/12/15 20:26
 *
 * @author Chiru
 */
public interface AnnouncementService extends CrudService<Announcement, Integer> {

    /**
     * 获取某社团的全部公告
     */
    List<AnnouncementDTO> getClubAnnouncements(@NonNull Integer clubId);

    /**
     * 创建公告
     */
    AnnouncementDTO createBy(@NonNull AnnouncementParam param);

    /**
     * 更新公告
     */
    AnnouncementDTO updateBy(@NonNull AnnouncementParam param);

    /**
     * 删除一条公告
     */
    AnnouncementDTO deleteOne(@NonNull Integer annoId);

    /**
     * 删除社团的全部公告
     */
    void deleteClubAllAnnouncements(@NonNull Integer clubId);
}
