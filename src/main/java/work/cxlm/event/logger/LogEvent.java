package work.cxlm.event.logger;

import org.springframework.context.ApplicationEvent;
import work.cxlm.model.enums.LogType;
import work.cxlm.model.params.LogParam;
import work.cxlm.utils.ServletUtils;
import work.cxlm.utils.ValidationUtils;

/**
 * created 2020/10/22 16:59
 *
 * @author johnniang
 * @author cxlm
 */
public class LogEvent extends ApplicationEvent {

    private final LogParam param;

    /**
     * 创建一个 ApplicationEvent
     *
     * @param source 导致该事件发生或与该时间直接关联的对象
     */
    public LogEvent(Object source, LogParam param) {
        super(source);

        //  验证 LogParam 实体
        ValidationUtils.validate(param);

        param.setIp(ServletUtils.getRequestIp());

        this.param = param;
    }

    /**
     * 系统级别日志事件
     */
    public LogEvent(Object source, Integer logKey, LogType type, String content) {
        this(source, new LogParam(logKey, type, content));
    }

    /**
     * 社团级别日志事件
     */
    public LogEvent(Object source, Integer logKey, Integer groupId, String content) {
        this(source, new LogParam(logKey, groupId, content));
    }

    public LogParam getParam() {
        return param;
    }
}
