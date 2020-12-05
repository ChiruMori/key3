package work.cxlm.model.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import work.cxlm.model.dto.base.OutputConverter;
import work.cxlm.model.entity.Room;

import java.math.BigDecimal;

/**
 * @program: myfont
 * @author: beizi
 * @create: 2020-11-20 12:53
 * @application :
 * @Version 1.0
 **/
@Data
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class RoomDTO extends RoomSimpleDTO implements OutputConverter<RoomDTO, Room> {

    @ApiModelProperty("经度")
    private BigDecimal longitude;

    @ApiModelProperty("纬度")
    private BigDecimal latitude;

    private Integer cost;

}
