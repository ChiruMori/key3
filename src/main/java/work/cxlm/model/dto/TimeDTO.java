package work.cxlm.model.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import work.cxlm.model.dto.base.OutputConverter;
import work.cxlm.model.entity.User;

/**
 * @author beizi
 * create 2020-11-21 20:43
 **/
@Data
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class TimeDTO extends SimpleUserDTO implements OutputConverter<TimeDTO, User> {

    private Integer timeId;

    private String name;
}
