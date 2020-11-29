package work.cxlm.model.params;

import io.swagger.annotations.ApiModelProperty;
import io.swagger.annotations.ApiOperation;
import lombok.Data;
import work.cxlm.model.dto.base.InputConverter;
import work.cxlm.model.entity.Bill;
import work.cxlm.model.support.CreateCheck;
import work.cxlm.model.support.UpdateCheck;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.math.BigDecimal;

/**
 * created 2020/11/29 18:39
 *
 * @author Chiru
 */
@Data
public class BillParam implements InputConverter<Bill> {

    @ApiModelProperty("收支 ID，即流水号，创建时为空，更新时必传。")
    private Integer id;

    @ApiModelProperty("收支金额")
    @NotNull(message = "收支不能为空")
    private BigDecimal cost;

    @ApiModelProperty("收支详情")
    @NotBlank(message = "社团名不可为空")
    @Size(max = 255)
    private String info;

    @NotNull(message = "必须指定所属社团")
    private Integer clubId;
}
