package work.cxlm.model.vo;

import lombok.Data;
import work.cxlm.model.dto.ClubDTO;
import work.cxlm.model.dto.RoomDTO;

import java.util.List;
import java.util.Map;

/**
 * created 2020/12/13 14:43
 *
 * @author Chiru
 */
@Data
public class ClubRoomMapVO {

    private List<ClubDTO> clubs;

    private Map<Integer, List<RoomDTO>> clubRooms;
}
