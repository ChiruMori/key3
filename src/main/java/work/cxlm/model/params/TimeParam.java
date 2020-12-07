package work.cxlm.model.params;


import io.swagger.annotations.ApiModelProperty;
import io.swagger.annotations.ApiParam;
import lombok.Data;
import org.hibernate.validator.constraints.Length;
import work.cxlm.model.dto.base.InputConverter;
import work.cxlm.model.entity.Room;
import work.cxlm.model.entity.TimePeriod;
import work.cxlm.model.enums.TimeState;
import work.cxlm.model.support.CreateCheck;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.Date;

/**
 * @author: beizi
 * @author Chiru
 * create 2020-11-22 17:39
 **/
@Data
public class TimeParam implements InputConverter<TimePeriod> {

    @NotNull(message = "预定的时间段的编号")
    @ApiModelProperty("时间段编号，编号规则：yyyyMMddHHmmssiiii 其中，iiii 表示四位的 roomID")
    private Long id;

    /**
     * 显示在时间表格中的文本
     */
    @Size(max = 8, message = "显示文本太长")
    private String showText;
}
