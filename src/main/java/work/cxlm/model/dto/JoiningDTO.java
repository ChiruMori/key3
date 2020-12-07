package work.cxlm.model.dto;

import lombok.Data;
import work.cxlm.model.dto.base.OutputConverter;
import work.cxlm.model.entity.Joining;

/**
 * created 2020/12/2 8:55
 *
 * @author Chiru
 */
@Data
public class JoiningDTO implements OutputConverter<JoiningDTO, Joining> {

    private String head;

    private Long studentNo;

    private String realName;

    private String position;

    private Integer total;

    private Integer point;

    private Boolean admin;

    private Integer absentCounter;

}
