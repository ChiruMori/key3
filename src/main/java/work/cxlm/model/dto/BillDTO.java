package work.cxlm.model.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import work.cxlm.model.dto.base.OutputConverter;
import work.cxlm.model.entity.Bill;

import java.math.BigDecimal;

/**
 * created 2020/11/26 14:35
 *
 * @author Chiru
 */
@Data
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class BillDTO extends DashboardListItem implements OutputConverter<BillDTO, Bill> {

    private Integer id;

    private BigDecimal cost;

    private String info;

    private Integer clubId;

    private Integer authorId;
}
