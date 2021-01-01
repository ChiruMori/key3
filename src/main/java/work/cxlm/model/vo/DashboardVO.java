package work.cxlm.model.vo;

import lombok.Data;
import org.springframework.data.domain.Page;
import work.cxlm.model.dto.BillDTO;
import work.cxlm.model.dto.LogDTO;

import java.math.BigDecimal;

/**
 * 向仪表盘页面传递数据的数据传输对象
 * created 2020/11/26 14:52
 *
 * @author Chiru
 */
@Data
public class DashboardVO {

    /**
     * 账单分页
     */
    private Page<BillDTO> bills;
    /**
     * 日志分页
     */
    private Page<LogDTO> logs;

    /**
     * 注册成员数
     */
    private Integer enrollMembers;
    /**
     * 激活成员数
     */
    private Integer activeMembers;
    /**
     * 可用资产
     */
    private BigDecimal assets;
    /**
     * 活动室使用率
     */
    private Float usage;
}
