package work.cxlm.model.vo;

import lombok.Data;
import work.cxlm.model.dto.TimePeriodSimpleDTO;

import java.util.List;

/**
 * created 2020/12/6 22:04
 *
 * @author Chiru
 */
@Data
public class TimeTableVO {

    private List<List<TimePeriodSimpleDTO>> timeTable;

    private List<String> timeTitle;

    private Integer week;
}
