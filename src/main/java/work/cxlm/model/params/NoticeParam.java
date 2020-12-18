package work.cxlm.model.params;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import work.cxlm.model.dto.base.InputConverter;
import work.cxlm.model.entity.Notice;
import work.cxlm.model.enums.NoticeType;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

/**
 * created 2020/12/10 14:53
 *
 * @author Chiru
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class NoticeParam implements InputConverter<Notice> {

    private NoticeType type;

    @Size(max = 50)
    private String title;

    @Size(max = 255)
    private String content;

    private Integer srcId;

    @NotNull
    private Integer targetUserId;

    private Boolean read;

    public NoticeParam(NoticeType type, String content, Integer srcId, Integer targetUserId) {
        this.type = type;
        this.title = type.getTitle();
        this.content = content;
        this.srcId = srcId;
        this.targetUserId = targetUserId;
    }

    public NoticeParam(NoticeType type, Integer srcId) {
        this.type = type;
        this.title = type.getTitle();
        this.srcId = srcId;
    }
}
