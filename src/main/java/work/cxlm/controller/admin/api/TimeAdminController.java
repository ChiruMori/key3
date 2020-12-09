package work.cxlm.controller.admin.api;

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import work.cxlm.model.params.TimeParam;
import work.cxlm.model.vo.TimeTableVO;
import work.cxlm.service.TimeService;

/**
 * created 2020/12/7 23:07
 *
 * @author Chiru
 */
@RestController
@RequestMapping("/key3/admin/api/")
public class TimeAdminController {

    private final TimeService timeService;

    public TimeAdminController(TimeService timeService) {
        this.timeService = timeService;
    }

    @ApiOperation(value = "获取活动室当前时间段表格，与用户同名接口完全相同", notes = "返回数据包括当前周次信息、时间表、时间标题信息。用于预约列表页的显示。")
    @GetMapping("table/{roomId}/{week}")
    public TimeTableVO getTimePeriod(@ApiParam("活动室 ID") @PathVariable("roomId") Integer roomId,
                                     @ApiParam("周次，当前周为 0，前一周为 -1，后一周为1") @PathVariable("week") Integer week){
        return timeService.getTimeTable(roomId, week);
    }

    @ApiOperation("管理员禁用时段")
    @PutMapping("block")
    public TimeTableVO blockTimePeriod(@Validated @RequestBody TimeParam param) {
        return timeService.blockBy(param);
    }

    @ApiOperation("管理员清除时段")
    @PutMapping("clear")
    public TimeTableVO clearTimePeriod(@Validated @RequestBody TimeParam param) {
        return timeService.clearBy(param);
    }

    @ApiOperation("管理员修改时段的显示文本")
    @PutMapping("text")
    public TimeTableVO changeTimeText(@Validated @RequestBody TimeParam param) {
        return timeService.changeTextBy(param);
    }

}
