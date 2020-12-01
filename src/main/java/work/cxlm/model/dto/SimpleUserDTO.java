package work.cxlm.model.dto;

import lombok.Data;
import work.cxlm.model.dto.base.OutputConverter;
import work.cxlm.model.entity.User;
import work.cxlm.model.enums.UserGender;

/**
 * 用户的简要信息
 * created 2020/11/18 12:09
 *
 * @author Chiru
 */
@Data
public class SimpleUserDTO {

    private String wxName;

    private String wxId;

    private String institute;

    private String major;

    private String realName;

    private UserGender gender;

    private String head;

    private String sign;
}
