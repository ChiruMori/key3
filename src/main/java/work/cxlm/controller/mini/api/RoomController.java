package work.cxlm.controller.mini.api;

import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import work.cxlm.model.dto.RoomDTO;
import work.cxlm.service.RoomService;

import java.util.List;

/**
 * 对接小程序，与活动室有关的接口
 *
 * @author beizi
 * @author Chiru
 * create: 2020-11-20 15:11
 */
@RestController
@RequestMapping("/key3/room/api")
public class RoomController {

    private final RoomService roomService;

    public RoomController(RoomService roomService) {
        this.roomService = roomService;
    }

    @GetMapping("/list/{clubId}")
    @ApiOperation(value = "获取某社团的全部活动室", notes = "需要传递社团 ID")
    public List<RoomDTO> getClubRooms(@PathVariable("clubId") Integer clubId) {
        return roomService.listClubRooms(clubId);
    }

    @GetMapping("/info/{roomId}")
    @ApiOperation(value = "获取某活动室信息", notes = "需要活动室 ID，得到的信息与 /list/{clubId} 中的信息一样")
    public RoomDTO getRoomInfo(@PathVariable("roomId") Integer roomId) {
        return roomService.getOne(roomId);
    }

}
