package work.cxlm.model.params;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import work.cxlm.model.enums.TimeState;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.ArrayList;

/**
 * @author beizi
 * @author Chiru
 * create 2020-11-22 17:39
 **/
@Data
public class TimeParam {

    @NotNull(message = "预定的时间段的编号数组，不能为 null")
    @ApiModelProperty("要操作的时间段编号数组，编号规则：yyyyMMddHHmmssiiii 其中，iiii 表示四位的 roomID")
    private ArrayList<Long> ids;

    @ApiModelProperty("显示的占位提示文本，可以留空")
    @Size(max = 8, message = "显示文本太长")
    private String showText;

    @ApiModelProperty("完成操作后，表格中需要显示的周次")
    @NotNull(message = "必须指定要显示的周，0 表示当前周")
    private Integer week;

    @ApiModelProperty(value = "禁用时需要传递，表示使用的颜色", notes = "合法值有三个：DISABLED_RED, DISABLED_WARM, DISABLED_COOL")
    private TimeState colorState;
}
