package work.cxlm.model.vo;

import io.swagger.annotations.ApiModelProperty;
import work.cxlm.model.dto.SimpleUserDTO;
import work.cxlm.model.dto.base.OutputConverter;
import work.cxlm.model.entity.Room;


/**
 * @author beizi
 * create 2020-11-20 15:27
 **/
public class PageRoomVO extends SimpleUserDTO implements OutputConverter<PageRoomVO, Room> {

    @ApiModelProperty("该活动室总时长")
    private Integer total;

    @ApiModelProperty("该活动室预约的总人数")
    private Integer people;


}
