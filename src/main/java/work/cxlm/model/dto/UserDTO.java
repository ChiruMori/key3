package work.cxlm.model.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.validator.constraints.Range;
import work.cxlm.model.dto.base.OutputConverter;
import work.cxlm.model.entity.User;
import work.cxlm.model.enums.UserGender;
import work.cxlm.model.enums.UserRole;

import javax.persistence.Column;
import java.util.Date;

/**
 * 用户的全部信息
 * created 2020/11/15 10:47
 *
 * @author johnniang
 * @author Chiru
 */
@Data
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class UserDTO extends SimpleUserDTO implements OutputConverter<UserDTO, User> {

    private Integer id;

    private Long studentNo;

    private String email;

    private UserRole role;

    private Boolean receiveMsg;
}
