package work.cxlm.model.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import work.cxlm.model.dto.SimpleUserDTO;
import work.cxlm.model.dto.base.OutputConverter;
import work.cxlm.model.entity.Joining;
import work.cxlm.model.entity.User;

/**
 * 用于在分页列表中显示的用户信息
 * created 2020/11/18 12:08
 *
 * @author Chiru
 */
@Data
@ToString
@EqualsAndHashCode(callSuper = true)
public class PageUserVO extends SimpleUserDTO implements OutputConverter<PageUserVO, User>{

    @ApiModelProperty("用户在该社团担任的职位")
    private String position;

    @ApiModelProperty("用户在该社团预订的活动室总时长")
    private Integer total;

    @ApiModelProperty("用户在该社团的贡献点数")
    private Integer point;

    @ApiModelProperty("用户是否为该社团的管理员")
    private Boolean admin;
}
