package work.cxlm.model.enums.converter;

import work.cxlm.model.enums.TimeState;

import javax.persistence.Converter;

/**
 * created 2020/12/7 22:01
 *
 * @author Chiru
 */
@Converter(autoApply = true)
public class TimeStateConverter extends AbstractConverter<TimeState, Integer> {

    protected TimeStateConverter() {
        super(TimeState.class);
    }
}
