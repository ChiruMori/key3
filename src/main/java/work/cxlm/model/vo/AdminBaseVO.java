package work.cxlm.model.vo;

import lombok.Data;
import work.cxlm.model.entity.Club;
import work.cxlm.model.enums.UserRole;

import java.util.List;

/**
 * created 2020/11/26 8:48
 *
 * @author Chiru
 */
@Data
public class AdminBaseVO {

    private String head;
    private String userName;
    private List<Club> clubs;

    private boolean systemAdmin;
    private boolean clubAdmin;
    private boolean showSideBarCard;
}
