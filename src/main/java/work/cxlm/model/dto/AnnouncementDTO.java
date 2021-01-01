package work.cxlm.model.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import work.cxlm.model.dto.base.OutputConverter;
import work.cxlm.model.entity.Announcement;

/**
 * created 2020/12/15 20:32
 *
 * @author Chiru
 */
@Data
@ToString
@EqualsAndHashCode(callSuper = true)
public class AnnouncementDTO extends BaseListItemDTO implements OutputConverter<AnnouncementDTO, Announcement> {
    private Integer id;
    private String title;
    private String content;
    private Integer publisherId;
    private Integer clubId;
}
