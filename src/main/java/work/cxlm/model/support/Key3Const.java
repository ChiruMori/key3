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
public class Key3Const {

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


    /**
     * 占位图 URL
     */
    public static final String ERROR_HEAD_URL = "https://cxlm.work/upload/2020/12/error-df59117371c948238cd6d68dba76449a.png";
    public static final String SYSTEM_HEAD = "https://cxlm.work/upload/2020/12/1010-d61f21ca80a9406c8e2fcf553b099ed1.png";

    /**
     * 活动室位置在缓存中使用的键
     */
    public static final String LOCATION_KEY = "room_locate";

    /**
     * 管理员验证 token 前缀
     */
    public static final String ADMIN_AUTH_KEY_PREFIX = "admin.";

    /**
     * 缓存模块：系统配置缓存键
     */
    public static final String OPTION_KEY = "options";

    /**
     * TODO: 显示在帮助页面的信息，这玩意放在别的地方
     */
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
                    "在预约表格中可以点击按钮以查看之前、之后的周次，点击周次显示区域可以回到当前周。"},
            {"用户信息",
                    "您可以通过修改信息补充个人信息，这些信息会展示给其他用户。",
                    "完善信息时，通过获取微信个人数据，可以更新您的头像、微信名、性别",
                    "在成员列表中看到的“领带用户”图标表示该用户为社团管理员",
                    "用户的点数：在后续版本可能会有其他用户的保留字段，目前并没有卵用"},
            {"用户角色",
                    "社团角色有如下三种：",
                    "♦ 系统管理员：拥有系统的最高权限，具有所有数据（如系统参数、以及社团管理员的全部权限）的访问、修改权限。通常负责系统维护、日常管理。",
                    "♦ 社团管理员、管理员角色：拥有与某社团相关数据（如用户、预定、活动室等）的完全访问、修改权限。",
                    "♦ 普通用户：没有特殊权限，可以使用小程序提供的全部功能，访问全部开放数据。但无法访问管理后台。"},
            {"通知公告",
                    "在您的通知页面可以看到与您相关的通知，这些通知通常不会打扰您",
                    "如果您在小程序中开启了接受通知，这些通知会同时推送到您的邮箱",
                    "通知类型有：公告通知、留言通知、活动室预约提醒、权限变更提醒等"},
            {"后台管理",
                    "如果您是管理员，则可以通过\"我\"页面的 Passcode 按钮生成一次性登录口令，用于登录管理后台",
                    "地址：cxlm.work/key3/admin/page/login"},
            {"获取帮助",
                    "在使用过程中遇到问题，可以联系系统管理员进行解决。也可以联系 cxlm@cxlm.work。",
                    "寻求帮助时，最好可以提供您的问题截图。"},
            {"V3.0版本",
                    "将后台管理功能从小程序中抽离，并实现为单独的系统。",
                    "增加社团信息、消息通知、公告、收支、用户信息、留言功能等",
                    "干掉一些 BUG，留下新的 BUG"},
            {"未来新增？",
                    "签到功能：为整治成员预约而不去活动室的情况，使用地理定位进行限制。因为会极大增加系统使用复杂度，可能导致用户使用起来的麻烦，故未添加。如果确实需要，可以反馈给管理员，如果需求很大，则会更新并增加。",
                    "积分机制：感觉没啥用，接口写好了，暂时没实际用途"}

    };
}
