package work.cxlm.model.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import work.cxlm.model.dto.base.OutputConverter;
import work.cxlm.model.entity.Notice;
import work.cxlm.model.enums.NoticeType;

/**
 * created 2020/12/10 15:10
 *
 * @author Chiru
 */
@Data
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class NoticeDTO extends BaseListItemDTO implements OutputConverter<NoticeDTO, Notice> {

    private Long id;

    private NoticeType type;

    private String title;

    private String content;

    private Integer srcId;

    private Boolean read;

}
