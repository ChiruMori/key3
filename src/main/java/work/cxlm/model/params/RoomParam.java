package work.cxlm.model.params;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import work.cxlm.model.dto.base.InputConverter;
import work.cxlm.model.entity.Room;
import work.cxlm.model.support.CreateCheck;
import work.cxlm.model.support.UpdateCheck;

import javax.validation.constraints.*;
import java.math.BigDecimal;

/**
 * created 2020/12/5 12:52
 *
 * @author Chiru
 */
@Data
public class RoomParam implements InputConverter<Room> {

    @NotNull(message = "社团 ID 不能为 null", groups = {UpdateCheck.class, CreateCheck.class})
    private Integer clubId;

    @ApiModelProperty("活动室 ID，如果希望将一个活动室归属到一个社团中，需要在创建社团时填写此项，其余时间留空")
    @NotNull(message = "活动室 ID 必传", groups = UpdateCheck.class)
    private Integer id;

    @NotBlank(message = "活动室名不能为空", groups = CreateCheck.class)
    private String name;

    @Digits(message = "超出范围: 168", integer = 168, fraction = 168, groups = {UpdateCheck.class, CreateCheck.class})
    private Integer weekLimit;

    @Digits(message = "超出范围: 24", integer = 24, fraction = 24, groups = {UpdateCheck.class, CreateCheck.class})
    private Integer dayLimit;

    @Min(message = "开始时间必须大于或等于 0", value = 0, groups = {UpdateCheck.class, CreateCheck.class})
    @Max(message = "开始时间必须小于 24", value = 23, groups = {UpdateCheck.class, CreateCheck.class})
    private Integer startHour;

    @Min(message = "结束时间必须大于 0", value = 1, groups = {UpdateCheck.class, CreateCheck.class})
    @Max(message = "结束时间必须小于或等于24", value = 24, groups = {UpdateCheck.class, CreateCheck.class})
    private Integer endHour;

    private Boolean needSign;

    @DecimalMin(message = "经度不能小于 -180", value = "-180", groups = {UpdateCheck.class, CreateCheck.class})
    @DecimalMax(message = "经度不能大于 180", value = "180", groups = {UpdateCheck.class, CreateCheck.class})
    private BigDecimal longitude;

    @DecimalMin(message = "纬度不能小于 -90", value = "-90", groups = {UpdateCheck.class, CreateCheck.class})
    @DecimalMax(message = "纬度不能大于 90", value = "90", groups = {UpdateCheck.class, CreateCheck.class})
    private BigDecimal latitude;

    @Digits(message = "超出范围", integer = Integer.MAX_VALUE, fraction = Integer.MAX_VALUE, groups = {UpdateCheck.class, CreateCheck.class})
    private Integer cost;

    private Boolean available;
}
