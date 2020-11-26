package work.cxlm.model.params;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import work.cxlm.model.dto.base.InputConverter;
import work.cxlm.model.entity.Log;
import work.cxlm.model.enums.LogType;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

/**
 * created 2020/10/22 17:01
 *
 * @author johnniang
 * @author cxlm
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class LogParam implements InputConverter<Log> {

    private Integer logKey;

    @NotNull(message = "日志类型不能为 null")
    private LogType type;

    @NotBlank(message = "日志内容不能为空")
    @Size(max = 1023, message = "日志内容必须小于 {max}")
    private String content;

    private String ip;

    public LogParam(Integer logKey, LogType type, String content) {
        this.logKey = logKey;
        this.type = type;
        this.content = content;
    }
}
