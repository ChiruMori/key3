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

    public static final String SYSTEM_HEAD = "https://cxlm.work/upload/2020/12/1010-d61f21ca80a9406c8e2fcf553b099ed1.png";
    // 占位图 URL
    public static final String ERROR_HEAD_URL = "https://cxlm.work/upload/2020/12/error-df59117371c948238cd6d68dba76449a.png";
    // 活动室位置缓存键
    public static final String LOCATION_KEY = "room_locate";

    // 显示在帮助页面的信息
    public static final String[][] USER_HELP = {
            {"授权说明",
                    "用户首次登录时，会要求输入学号用于对当前微信账号进行绑定。",
                    "此绑定只发生一次，如果因误操作导致绑定了错误的用户数据，需要用户联系管理员手动修改。"},
            {"基本使用",
                    "只有被管理员添加的用户才能使用小程序，用户在绑定学号后，通常在进入小程序后会自动登录到主页面。",
                    "用户有多个活动室可选时会要求用户在进入前选择，您也可以在进入后，使用预约表格下方的\"重新进入\"按钮进行重新选择"},
            {"时段预约",
                    "在时间表格中的空白部分点击即可完成预约，再次点击可以取消。",
                    "用户的预约数不能超过社团设置的日上限、周上限。但是，已经过去的时间不进行统计。",
                    "在预约表格中看到的各种颜色解释如下：",
                    "♦ 无色：此时段空闲，可以进行预约",
                    "♦ 黄色：此时段已过期或尚未开放",
                    "♦ 蓝色：其他用户预约的时段",
                    "♦ 绿色：您预约的时段",
                    "♦ 红色、渐变色：管理员因某些事项占用的时段，不可预约",
                    "在预约表格中可以点击周次显示区域以回到当前周。"},
            {"用户信息",
                    "您可以通过修改信息补充个人信息，这些信息会展示给其他用户。",
                    "完善信息时，通过获取微信个人数据，可以更新您的头像、微信名、性别"},
            {"后台管理",
                    "如果您是管理员，则可以通过\"我\"页面的 Passcode 按钮生成一次性登录口令，用于登录管理后台",
                    "地址：https://cxlm.work/key3/admin/page/login"},
            {"获取帮助",
                    "在使用过程中遇到问题，可以联系系统管理员进行解决。也可以联系 cxlm@cxlm.work。",
                    "寻求帮助时，最好可以提供您的问题截图。"},
            {"V3.0版本",
                    "将后台管理功能从小程序中抽离，并实现为单独的系统。",
                    "干掉一些 BUG，留下新的 BUG"}
    };
}
