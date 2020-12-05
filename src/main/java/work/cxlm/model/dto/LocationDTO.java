package work.cxlm.model.dto;

import lombok.Data;

import java.math.BigDecimal;

/**
 * created 2020/12/5 15:32
 *
 * @author Chiru
 */
@Data
public class LocationDTO {

    /**
     * 数据编号
     */
    private Integer dataId;

    /**
     * 经度
     */
    private BigDecimal longitude;

    /**
     * 纬度
     */
    private BigDecimal latitude;
}
