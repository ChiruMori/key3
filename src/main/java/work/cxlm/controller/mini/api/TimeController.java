package work.cxlm.controller.mini.api;

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import work.cxlm.model.params.TimeParam;
import work.cxlm.model.vo.TimeTableVO;
import work.cxlm.service.*;

/**
 * 时段预订相关 API 接口
 * created 2020/11/16 19:24
 *
 * @author Chiru
 */
@Slf4j
@RestController
@RequestMapping("/key3/time/api/")
public class TimeController {

    private TimeService timeService;

    @Autowired
    public void setTimeService(TimeService timeService) {
        this.timeService = timeService;
    }

    @ApiOperation(value = "获取活动室当前可用的时间段", notes = "返回数据包括当前周次信息、时间表、时间标题信息。用于预约列表页的显示。")
    @GetMapping("/table/{roomId}/{week}")
    public TimeTableVO getTimePeriod(@ApiParam("活动室 ID") @PathVariable("roomId") Integer roomId,
                                     @ApiParam("周次，当前周为 0，前一周为 -1，后一周为1") @PathVariable("week") Integer week){
        return timeService.getTimeTable(roomId, week);
    }

    @ApiOperation(value = "预约时间段", notes = "用户预约指定时段，另外需要传递 access token")
    @PostMapping("/occupy/{timeId}")
    public void orderTimePeriod(@ApiParam("时间 ID") @PathVariable("timeId") Long timeId){
        timeService.occupyTimePeriod(timeId);
    }

    @ApiOperation(value = "取消预约时间段", notes = "用户取消指定时段的预约，同时需要传递 access token")
    @DeleteMapping("/cancel/{timeId}")
    public void noTimePeriod(@ApiParam("时间 ID") @PathVariable("timeId") Long timeId){
        timeService.cancelTimePeriod(timeId);
    }
}
