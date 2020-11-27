package work.cxlm.model.dto;

import lombok.Data;
import org.hibernate.annotations.ColumnDefault;
import work.cxlm.model.dto.base.OutputConverter;
import work.cxlm.model.entity.Club;

import java.math.BigDecimal;
import java.util.Date;

/**
 * created 2020/11/27 15:02
 *
 * @author Chiru
 */
@Data
public class ClubDTO implements OutputConverter<ClubDTO, Club> {

    private Integer id;

    private String name;

    private BigDecimal assets;

    private Boolean billEnabled;

    private Date createTime;
}
