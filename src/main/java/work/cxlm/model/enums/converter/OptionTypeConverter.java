package work.cxlm.model.enums.converter;

import work.cxlm.model.enums.OptionType;

import javax.persistence.Converter;

/**
 * OptionType 的转换器
 * created 2020/11/20 18:57
 *
 * @author Chiru
 */
@Deprecated
@Converter(autoApply = true)
public class OptionTypeConverter extends AbstractConverter<OptionType, Integer> {

    public OptionTypeConverter() {
        super(OptionType.class);
    }
}
