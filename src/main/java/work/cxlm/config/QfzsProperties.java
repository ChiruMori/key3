package work.cxlm.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import work.cxlm.model.enums.Mode;
import work.cxlm.utils.QfzsUtils;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;

import static work.cxlm.model.support.QfzsConst.FILE_SEPARATOR;
import static work.cxlm.model.support.QfzsConst.USER_HOME;

/**
 * 系统配置
 * created 2020/11/1 17:08
 *
 * @author johnniang
 * @author cxlm
 */
@Data
@ConfigurationProperties("qfzs")
public class QfzsProperties {

    /**
     * 上传路径前缀
     */
    private String uploadUrlPrefix = "upload";

    private String workDir = QfzsUtils.ensureSuffix(USER_HOME, FILE_SEPARATOR) + ".my_font" + FILE_SEPARATOR;

    /**
     * 缓存保存的位置
     * 可选：memory, level, redis
     */
    private String cache = "level";

    /**
     * 可用的 Redis 节点
     */
    private List<String> redisNodes = new ArrayList<>();

    private String redisPwd = "REDIS_mima~9749";

    @PostConstruct
    private void init() {
        redisNodes.add("127.0.0.1:6543");  // 配置使用的 Redis 节点
    }

    /**
     * 系统运行模式
     */
    private Mode mode = Mode.PRODUCTION;

    /**
     * 是否为生产环境，默认为是
     */
    private boolean productionEnv = true;

    /**
     * 是否启用文档接口. 默认启用
     */
    private boolean docDisabled = true;

    /**
     * 版本号
     */
    private String version = "unknown";

    /**
     * 小程序 ID
     */
    private String appId = "";

    /**
     * 小程序密钥
     */
    private String appSecret = "";

    /**
     * 请求 URL
     */
    private String appRequestUrl = "https://api.weixin.qq.com/sns/jscode2session";

    private String systemAdminDoc = "https://cxlm.work/s/key3-system-admin-doc";

    private String clubAdminDoc = "https://cxlm.work/s/key3-club-admin-doc";

    private String userDoc = "https://cxlm.work/s/key3-user-doc";

}
