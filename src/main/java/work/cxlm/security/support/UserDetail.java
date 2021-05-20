package work.cxlm.security.support;

import lombok.*;
import org.springframework.lang.NonNull;
import work.cxlm.model.entity.User;

/**
 * 用户详情，包装了用户对象
 * created 2020/11/9 9:27
 *
 * @author johnniang
 * @author Chiru
 */
@ToString
@EqualsAndHashCode
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class UserDetail {

    private User user;
}
