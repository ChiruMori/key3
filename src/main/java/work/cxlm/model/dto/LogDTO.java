package work.cxlm.model.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import work.cxlm.model.dto.base.OutputConverter;
import work.cxlm.model.entity.Log;
import work.cxlm.model.enums.LogType;

/**
 * created 2020/10/29 15:18
 *
 * @author johnniang
 * @author cxlm
 */
@Data
@ToString
@EqualsAndHashCode(callSuper = true)
public class LogDTO extends BaseListItemDTO implements OutputConverter<LogDTO, Log> {

    private Long id;

    private LogType type;

    private String content;

    private String ip;

    private Integer logKey;
    private Integer groupId;
}
