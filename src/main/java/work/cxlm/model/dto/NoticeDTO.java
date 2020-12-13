package work.cxlm.model.dto;

import work.cxlm.model.dto.base.OutputConverter;
import work.cxlm.model.entity.Notice;
import work.cxlm.model.enums.NoticeType;

import javax.persistence.Column;

/**
 * created 2020/12/10 15:10
 *
 * @author Chiru
 */
public class NoticeDTO extends ListItemDTO implements OutputConverter<NoticeDTO, Notice> {

    private Long id;

    private NoticeType type;

    private String title;

    private String content;

    private Integer srcUserId;

    private Boolean read;

}
