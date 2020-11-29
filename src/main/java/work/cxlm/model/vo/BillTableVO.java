package work.cxlm.model.vo;

import lombok.Data;
import work.cxlm.model.dto.BillDTO;

import java.math.BigDecimal;
import java.util.List;

/**
 * created 2020/11/29 11:36
 *
 * @author Chiru
 */
@Data
public class BillTableVO {

    private List<BillDTO> bills;

    private BigDecimal clubAssets;
}
