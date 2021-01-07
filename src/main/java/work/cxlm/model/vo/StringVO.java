package work.cxlm.model.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * created 2021/1/7 14:52
 *
 * @author Chiru
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class StringVO {

    private String value;

    public static StringVO from(String value) {
        return new StringVO(value);
    }
}
