package work.cxlm.controller.mini.api;

import io.swagger.annotations.ApiOperation;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import work.cxlm.model.entity.Club;
import work.cxlm.model.entity.Room;
import work.cxlm.model.entity.TimePeriod;
import work.cxlm.model.entity.User;
import work.cxlm.model.params.TimeParam;
import work.cxlm.model.params.UserParam;
import work.cxlm.service.*;
import work.cxlm.service.impl.TimeServiceImpl;

import java.util.List;

import static work.cxlm.model.enums.TimeState.Time_IDLE;
import static work.cxlm.model.enums.TimeState.Time_ORDER;

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

    @ApiOperation(value = "预约时间段", notes = "id  roomId  StudentNo 是必填 token ")
    @PostMapping("/user/order")
    public void orderTimePeriod(@NonNull TimeParam time){
        timeService.orderTimePeriod(time);
        System.out.println("-------------------");
    }

    @ApiOperation(value = "关注时间段")
    @PostMapping("/user/att")
    public void attTimePeriod(@NonNull TimeParam time){
        timeService.attTimePeriod(time);
    }

    @ApiOperation(value = "取消预约时间段")
    @PostMapping("/user/noorder")
    public void noTimePeriod(@NonNull TimeParam time){
        timeService.noOrderTimePeriod(time);
    }

    @ApiOperation(value = "管理员禁用时间段")
    @PostMapping("/admin/banTime")
    public void banTimePeriod(@NonNull TimeParam time){
        timeService.adminStop(time.getId());
    }

    @ApiOperation(value = "管理员恢复时间段")
    @PostMapping("/admin/allowTime")
    public void allowTimePeriod(@NonNull TimeParam time){
        timeService.adminAllow(time.getId());
    }

    @ApiOperation(value = "获取所有的时间段")
    @PostMapping("/user/getRoom")
    public List<TimePeriod> getTimePeriod(@NonNull TimeParam time){
        return timeService.listAllPeriod(time.getRoomId());
    }



}
