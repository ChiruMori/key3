package work.cxlm.model.params;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import work.cxlm.model.dto.base.InputConverter;
import work.cxlm.model.entity.Club;
import work.cxlm.model.support.CreateCheck;
import work.cxlm.model.support.UpdateCheck;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.math.BigDecimal;

/**
 * created 2020/11/27 15:24
 *
 * @author Chiru
 */
@Data
public class ClubParam implements InputConverter<Club> {

    @ApiModelProperty("社团 ID，更新时必填")
    @NotNull(message = "社团 ID 不能为 null", groups = UpdateCheck.class)
    private Integer clubId;

    @ApiModelProperty("社团名，必填")
    @NotBlank(message = "社团名不可为空", groups = {UpdateCheck.class, CreateCheck.class})
    @Size(max = 50)
    private String name;

    @ApiModelProperty("初始资产")
    private BigDecimal assets;

    @ApiModelProperty("是否开启财务功能")
    private Boolean billEnabled;

    @ApiModelProperty("社团允许每周不签到的次数")
    private Integer absentLimit;
}
