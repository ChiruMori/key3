package work.cxlm.controller.mini.api;

import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import work.cxlm.model.dto.ClubDTO;
import work.cxlm.model.dto.RoomDTO;
import work.cxlm.model.vo.ClubRoomMapVO;
import work.cxlm.service.ClubService;

import java.util.List;

/**
 * created 2020/12/13 14:05
 *
 * @author Chiru
 */
@RestController
@RequestMapping("/key3/club/api")
public class ClubController {

    private final ClubService clubService;

    public ClubController(ClubService clubService) {
        this.clubService = clubService;
    }

    @GetMapping("/list")
    @ApiOperation(value = "获取某用户的全部社团", notes = "需要传递 token")
    public List<ClubDTO> getClubRooms() {
        return clubService.listUserClubs();
    }

    @GetMapping("/info/{clubId}")
    @ApiOperation(value = "获取某活动室信息", notes = "需要社团 ID，得到的信息与 /list 中的信息一样")
    public ClubDTO getRoomInfo(@PathVariable("clubId") Integer clubId) {
        return new ClubDTO().convertFrom(clubService.getById(clubId));
    }

    @GetMapping("/map")
    @ApiOperation(value = "获取社团、活动室的映射关系", notes = "需要传递 token")
    public ClubRoomMapVO getClubRoomMap() {
        return clubService.buildClubRoomMapOfUser();
    }
}
