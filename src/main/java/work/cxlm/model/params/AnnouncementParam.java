package work.cxlm.model.params;

import lombok.Data;
import work.cxlm.model.dto.base.InputConverter;
import work.cxlm.model.entity.Announcement;

import javax.persistence.Column;

/**
 * created 2020/12/15 20:29
 *
 * @author Chiru
 */
@Data
public class AnnouncementParam implements InputConverter<Announcement> {

    private Integer id;
    private String title;
    private String content;
    private Integer clubId;
}
