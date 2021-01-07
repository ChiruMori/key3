package work.cxlm.model.params;

import lombok.Data;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

/**
 * created 2021/1/6 21:38
 *
 * @author Chiru
 */
@Data
public class CacheEntryParam {

    @NotNull
    private String key;

    @NotEmpty
    private String value;
}
