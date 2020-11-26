package work.cxlm.model.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import work.cxlm.model.dto.base.OutputConverter;
import work.cxlm.model.entity.Log;
import work.cxlm.model.enums.LogType;

import java.util.Date;

/**
 * created 2020/10/29 15:18
 *
 * @author johnniang
 * @author cxlm
 */
@Data
@ToString
@EqualsAndHashCode
public class LogDTO implements OutputConverter<LogDTO, Log> {

    private Long id;

    private LogType type;

    private String content;

    private String ip;

    private Integer logKey;

    private Date createTime;

    private String showHead;

    private String who;
}
