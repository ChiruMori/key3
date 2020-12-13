package work.cxlm.controller.admin.api;

import io.swagger.annotations.ApiOperation;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import work.cxlm.model.dto.LocationDTO;
import work.cxlm.model.dto.RoomDTO;
import work.cxlm.model.params.RoomParam;
import work.cxlm.model.support.CreateCheck;
import work.cxlm.model.support.UpdateCheck;
import work.cxlm.service.RoomService;

import java.util.List;

/**
 * 房间基本信息的管理
 * created 2020/12/5 12:47
 *
 * @author Chiru
 */
@RestController
@RequestMapping("/key3/admin/api/")
public class AdminRoomController {

    private final RoomService roomService;

    public AdminRoomController(RoomService roomService) {
        this.roomService = roomService;
    }

    @PostMapping("room")
    @ApiOperation("通过表单新建一个活动室，并归属到当前社团下")
    public RoomDTO newClubRoom(@Validated(CreateCheck.class) @RequestBody RoomParam param) {
        return roomService.newRoomBy(param);
    }

    @PutMapping("room")
    @ApiOperation("更新活动室信息")
    public RoomDTO updateRoom(@Validated(UpdateCheck.class) @RequestBody RoomParam param) {
        return roomService.updateRoomBy(param);
    }

    @GetMapping("room/{clubId:\\d+}")
    @ApiOperation("获取指定活动室的所有活动室")
    public List<RoomDTO> getClubRooms(@PathVariable Integer clubId) {
        return roomService.listClubRooms(clubId);
    }

    @DeleteMapping("room/{clubId:\\d+}/{roomId:\\d+}")
    @ApiOperation("删除某个活动室")
    public RoomDTO deleteRoom(@PathVariable Integer clubId, @PathVariable Integer roomId) {
        return roomService.deleteRoom(clubId, roomId);
    }

    @ApiOperation("获取当前系统中缓存着的活动室位置")
    @GetMapping("room/locations/{clubId:\\d+}")
    public List<LocationDTO> getClubRoomLocations(@PathVariable("clubId") Integer clubId) {
        return roomService.getLocations(clubId);
    }
}
