package work.cxlm.model.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Date;

/**
 * 补充一些属性的 OptionDTO 数据传输（输出）数据传输类
 * created 2020/11/9 20:10
 *
 * @author ryanwang
 * @author Chiru
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class OptionSimpleDTO extends OptionDTO {

    private Integer id;
    private Date createTime;
    private Date updateTime;
}
