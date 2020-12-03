package work.cxlm.model.support;

import org.springframework.http.HttpHeaders;

import java.io.File;

/**
 * 公共常量
 * created 2020/11/1 23:17
 *
 * @author ryanwang
 * @author cxlm
 */
public class QfzsConst {

    /**
     * 用户路径
     */
    public static final String USER_HOME = System.getProperties().getProperty("user.home");

    /**
     * 文件分隔符
     */
    public static final String FILE_SEPARATOR = File.separator;

    /**
     * 路径分隔符
     */
    public static final String URL_SEPARATOR = "/";

    /**
     * freemarker 模板文件后缀名
     */
    public static final String SUFFIX_FTL = ".ftl";

    /**
     * Admin token header name.
     */
    public final static String ADMIN_TOKEN_HEADER_NAME = "ADMIN-" + HttpHeaders.AUTHORIZATION;
    /**
     * Admin token param name.
     */
    public final static String ADMIN_TOKEN_QUERY_NAME = "admin_token_" + HttpHeaders.AUTHORIZATION;
    /**
     * Content token header name.
     */
    public final static String API_ACCESS_KEY_HEADER_NAME = "API-" + HttpHeaders.AUTHORIZATION;
    /**
     * Content api token param name
     */
    public final static String API_ACCESS_KEY_QUERY_NAME = "api_access_key";
    /**
     * 一次性口令查询键
     */
    public final static String ONE_TIME_TOKEN_QUERY_NAME = "ott";
    public final static String ONE_TIME_TOKEN_HEADER_NAME = "ott";

    /**
     * 微信小程序登录凭证验证使用的键
     */
    public final static String WX_MINI_TOKEN_HEADER_NAME = "MINI-" + HttpHeaders.AUTHORIZATION;
    /**
     * 微信小程序登录凭证验证使用的键
     */
    public final static String WX_MINI_TOKEN_QUERY_NAME = "mini_token_" + HttpHeaders.AUTHORIZATION;

    /**
     * 默认个性签名
     */
    public final static String DEFAULT_USER_SIGNATURE = "这家伙很懒，什么都没写。。。";

    /**
     * openId 缓存用户时的缓存前缀
     */
    @Deprecated
    public final static String USER_CACHE_PREFIX = "user_";

    /**
     * 管理员登录时使用的 Passcode 缓存前缀
     */
    public final static String ADMIN_PASSCODE_PREFIX = "admin_pass_";

    public final static String DEFAULT_ERROR_PATH = "error/error";

    public static final String SYSTEM_HEAD = "https://upload.cc/i1/2020/11/26/HSBol9.png";
    // 占位图 URL
    public static final String ERROR_HEAD_URL = "https://upload.cc/i1/2020/11/30/561A98.png";
}
