package work.cxlm.model.params;

import lombok.Data;
import work.cxlm.model.entity.Option;
import work.cxlm.model.dto.base.InputConverter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

/**
 * created 2020/11/9 15:44
 *
 * @author Chiru
 */
@Data
public class OptionParam implements InputConverter<Option> {

    @NotBlank(message = "配置项的键不能为空")
    @Size(max = 100, message = "配置项的键长度不能超过 {max}")
    private String key;

    @Size(max = 1023, message = "配置项值的长度不能超过 {max}")
    private String value;
}
