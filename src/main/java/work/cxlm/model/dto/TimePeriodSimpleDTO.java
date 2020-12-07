package work.cxlm.model.dto;

import lombok.Data;
import work.cxlm.model.dto.base.OutputConverter;
import work.cxlm.model.entity.TimePeriod;
import work.cxlm.model.enums.TimeState;

/**
 * created 2020/12/6 18:26
 *
 * @author Chiru
 */
@Data
public class TimePeriodSimpleDTO implements OutputConverter<TimePeriodSimpleDTO, TimePeriod> {

    private Long id;
    private Integer userId;
    private String showText;
    private TimeState state;
}
