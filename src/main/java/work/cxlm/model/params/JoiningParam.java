package work.cxlm.model.params;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import work.cxlm.model.dto.base.InputConverter;
import work.cxlm.model.entity.Joining;
import work.cxlm.model.support.CreateCheck;
import work.cxlm.model.support.UpdateCheck;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

/**
 * created 2020/11/21 21:32
 *
 * @author Chiru
 */
@Data
public class JoiningParam implements InputConverter<Joining> {

    @NotNull(message = "必须填写学号", groups = {CreateCheck.class, UpdateCheck.class})
    private Long studentNo;

    @NotNull(message = "必须指定社团", groups = {CreateCheck.class, UpdateCheck.class})
    private Integer clubId;

    @ApiModelProperty("用户真实姓名，只在系统中不存在该用户时必传")
    @Size(max = 50, message = "真实姓名长度必须小于 {max}")
    private String realName;

    @Size(max = 50, message = "职务名长度必须小于 {max}", groups = {CreateCheck.class, UpdateCheck.class})
    private String position;

    private Boolean admin;

    @ApiModelProperty("社团贡献点数")
    private Integer point;

    @ApiModelProperty("活动室使用总时长")
    private Integer total;
}
