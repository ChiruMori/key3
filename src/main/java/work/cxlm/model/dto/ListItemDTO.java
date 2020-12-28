package work.cxlm.model.dto;

import lombok.Data;
import org.apache.commons.lang3.StringUtils;
import org.springframework.lang.Nullable;
import work.cxlm.model.entity.User;
import work.cxlm.model.support.QfzsConst;

import java.util.Date;

/**
 * created 2020/11/29 11:42
 *
 * @author Chiru
 */
@Data
public abstract class ListItemDTO {
    private String who;
    private String showHead;
    private Date createTime;

    public void fromUserData(@Nullable User targetUser) {
        if (targetUser == null) {
            who = "不明人士";
            showHead = QfzsConst.SYSTEM_HEAD;
            return;
        }
        who = targetUser.getRealName();
        // 头像，如果无效则使用占位图
        showHead = targetUser.getHead();
        if (StringUtils.isBlank(showHead)) {
            showHead = QfzsConst.ERROR_HEAD_URL;
        }
    }
}
