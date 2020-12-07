package work.cxlm.model.dto;

import lombok.Data;


/**
 * @author beizi
 * @author Chiru
 * create: 2020-11-20 14:13
 */
@Data
public class RoomSimpleDTO {

    private String name;

    private Integer weekLimit;

    private Integer dayLimit;

    private Integer startHour;

    private Integer endHour;

    private Boolean needSign;

    private Integer id;

}
