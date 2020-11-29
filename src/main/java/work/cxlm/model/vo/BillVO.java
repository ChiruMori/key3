package work.cxlm.model.vo;

import lombok.Data;
import lombok.EqualsAndHashCode;
import work.cxlm.model.dto.BillDTO;

import java.math.BigDecimal;

/**
 * created 2020/11/29 18:57
 *
 * @author Chiru
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class BillVO extends BillDTO {

    private BigDecimal clubAssets;
}
