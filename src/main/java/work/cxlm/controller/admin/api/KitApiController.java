package work.cxlm.controller.admin.api;

import io.swagger.annotations.ApiOperation;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import work.cxlm.model.params.CacheEntryParam;
import work.cxlm.model.vo.StringVO;
import work.cxlm.service.MonitorService;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * created 2021/1/6 21:30
 *
 * @author Chiru
 */
@RestController
@RequestMapping("/key3/kit/api/")
public class KitApiController {

    private final MonitorService monitorService;

    public KitApiController(MonitorService monitorService) {
        this.monitorService = monitorService;
    }

    @ApiOperation("列出全部有日志生成的日期")
    @GetMapping("dates")
    public List<Date> getLogDates() {
        return monitorService.datesHasLog();
    }

    @ApiOperation("获取某日的日志内容")
    @GetMapping("log/{timestamp}")
    public StringVO getDayLogContent(@PathVariable("timestamp") Long targetDate) {
        String responseText = monitorService.getDateLog(new Date(targetDate));
        return StringVO.from(responseText);
    }

    @ApiOperation("根据查询条件查询匹配的缓存（K-V 键值对）")
    @GetMapping("getCache")
    public Map<String, String> getCache(@RequestParam("type") String type, @RequestParam("addParam") String val) {
        return monitorService.getCachedData(type, val);
    }

    @ApiOperation("新建（更新）缓存")
    @PostMapping("updateCache")
    public void updateCache(@RequestBody @Validated CacheEntryParam param) {
        monitorService.setCache(param.getKey(), param.getValue());
    }

    @ApiOperation("删除某条缓存")
    @DeleteMapping("deleteCache/{key}")
    public void deleteOneCache(@PathVariable("key") String key) {
        monitorService.deleteCache(key);
    }

    @ApiOperation("清除全部缓存，包括缓存中间件、应用缓存")
    @DeleteMapping("clearCache")
    public void clearCache() {
        monitorService.killAllCacheData();
    }
}
