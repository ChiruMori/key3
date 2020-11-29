package work.cxlm.model.dto;

import lombok.Data;

import java.util.Date;

/**
 * created 2020/11/29 11:42
 *
 * @author Chiru
 */
@Data
public abstract class DashboardListItem {
    private String who;
    private String showHead;
    private Date createTime;
}
