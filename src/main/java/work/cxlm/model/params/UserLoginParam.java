package work.cxlm.model.params;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.hibernate.validator.constraints.Range;
import work.cxlm.model.dto.base.InputConverter;
import work.cxlm.model.entity.User;
import work.cxlm.model.support.CreateCheck;
import work.cxlm.model.support.UpdateCheck;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

/**
 * 用户登录初始化表单
 * created 2020/10/21 23:29
 *
 * @author johnniang
 * @author cxlm
 */
@Data
public class UserLoginParam {

    @ApiModelProperty("code 调用 wx.login 后获取到的 code 用于生成 openId")
    private String code;

    @ApiModelProperty("如果前端已经拿到 openId 则传递此参数进行登录，传递此参数时，无需传递 code")
    private String wxId;

}

