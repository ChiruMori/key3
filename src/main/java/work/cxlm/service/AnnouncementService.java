package work.cxlm.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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
     *
     * @param clubId 目标社团
     * @return 社团的全部公告 DTO 列表
     */
    List<AnnouncementDTO> getClubAnnouncements(@NonNull Integer clubId);

    /**
     * 创建公告
     *
     * @param param 管理后台填写的公告数据表单
     * @return 创建的公告 DTO
     */
    AnnouncementDTO createBy(@NonNull AnnouncementParam param);

    /**
     * 更新公告
     *
     * @param param 管理后台填写的公告数据表单
     * @return 修改后的公告 DTO
     */
    AnnouncementDTO updateBy(@NonNull AnnouncementParam param);

    /**
     * 删除一条公告
     *
     * @param annoId 目标公告 id
     * @return 被删除的公告 DTO
     */
    AnnouncementDTO deleteOne(@NonNull Integer annoId);

    /**
     * 删除社团的全部公告
     *
     * @param clubId 目标社团
     */
    void deleteClubAllAnnouncements(@NonNull Integer clubId);

    /**
     * 分页查询某社团的全部公告
     *
     * @param clubId   社团 ID
     * @param pageable 分页参数
     * @return 指定社团的公告数据分页数据集
     */
    Page<AnnouncementDTO> pageClubAnnouncements(Integer clubId, Pageable pageable);

    /**
     * 通过公告 ID 得到 Announcement 的 DTO 实体
     * @param annoId 目标公告 ID
     * @return DTO 包装的 Announcement 实体
     */
    AnnouncementDTO getAnnouncementDtoById(Integer annoId);
}
