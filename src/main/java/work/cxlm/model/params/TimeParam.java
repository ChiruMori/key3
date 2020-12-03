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
 * @program: myfont
 * @author: beizi
 * @create: 2020-11-22 17:39
 * @application :
 * @Version 1.0
 **/
@Data
public class TimeParam implements InputConverter<TimePeriod> {


    @NotBlank(message = "预定的时间段的编号", groups = CreateCheck.class)
    @ApiModelProperty("所要预约的时间段，默认只有1 2 3三个时间段")
    private Integer id;

    /**
     * 时段开始时间
     */

    private Date startTime;

    /**
     * 时段结束时间
     */
    private Date endTime;

    /**
     * 关联的活动室 ID
     */
    @NotBlank(message = "时间段关联的活动室，默认1", groups = CreateCheck.class)
    @ApiModelProperty("时间段关联的活动室，默认1")
    private Integer roomId;

    /**
     * 预订了当前时段的用户，可以为空，
     */
    @ApiModelProperty("进行预约操作的学生ID  可以为空")
    private Integer userId;

    /**
     * 显示在时间表格中的文本
     */

    @Length(max = 8)
    private String showText;

    /**
     * 当前时段是否已签到
     */

    private Boolean signed;

    /**
     * 用户是否在当前时段迟到，缺席并不算迟到
     */

    private Boolean late;

    @NotBlank(message = "学号绑定去预约时间段", groups = CreateCheck.class)
    @ApiModelProperty("对时间段进行操作，所需要的的学生学号 ")
    private Long studentNo;

    @Size(max = 30, message = "openId 长度不能超过 {max}")
    private String wxId;



}
