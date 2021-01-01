package work.cxlm.utils;

import org.apache.commons.lang3.StringUtils;
import org.springframework.lang.NonNull;
import org.springframework.util.Assert;

import java.util.UUID;

/**
 * created 2020/10/28 17:21
 *
 * @author cxlm
 */
public class QfzsUtils {

    /**
     * 将字符串的指定部分替换为 * 号
     *
     * @param plainText 原始文本
     * @param left      左边开始替换的位置
     * @param right     右边开始替换的字位置
     * @return 替换后的结果
     */
    public static String desensitize(String plainText, int left, int right) {
        Assert.hasText(plainText, "原始文本不能为空");

        if (left < 0) {
            left = 0;
        }
        if (right < 0) {
            right = 0;
        }

        int len = plainText.length();

        if (left > len) {
            left = len;
        }
        if (right > len) {
            right = len;
        }

        if (left + right > len) {
            right = len - left;
        }
        int remainSize = len - left - right;
        String leftStr = StringUtils.left(plainText, left);
        String rightStr = StringUtils.right(plainText, right);
        return StringUtils.rightPad(leftStr, remainSize + left, '*') + rightStr;
    }

    /**
     * 格式化时间
     *
     * @param seconds 秒数
     */
    public static String timeFormat(long seconds) {
        if (seconds <= 0) {
            return "0 秒";
        }

        StringBuilder timeBuilder = new StringBuilder();
        long hour = seconds / 3600;
        long minute = seconds % 3600 / 60;
        seconds = seconds % 3600 % 60;

        if (hour > 0) {
            if (StringUtils.isNotBlank(timeBuilder)) {
                timeBuilder.append(", ");
            }
            timeBuilder.append(hour).append(" ").append("时");
        }
        if (minute > 0) {
            if (StringUtils.isNotBlank(timeBuilder)) {
                timeBuilder.append(", ");
            }
            timeBuilder.append(minute).append(" ").append("分");
        }
        if (seconds > 0) {
            if (StringUtils.isNotBlank(timeBuilder)) {
                timeBuilder.append(", ");
            }
            timeBuilder.append(seconds).append(" ").append("秒");
        }
        return timeBuilder.toString();
    }

    /**
     * Pluralize the times label format.
     * Only For English, Override this function for more language
     *
     * @param times  times
     * @param label  label
     * @param plural plural label
     * @return pluralized format
     */
    @NonNull
    public static String pluralize(long times, @NonNull String label, @NonNull String plural) {
        Assert.hasText(label, "Label must not be blank");
        Assert.hasText(plural, "Plural label must not be blank");

        if (times <= 0) {
            return "no " + plural;
        }

        if (times == 1) {
            return times + " " + label;
        }

        return times + " " + plural;
    }

    /**
     * 确保字符串包含指定前缀，如果已包含则不变，否则添加前缀
     *
     * @param str    源字符串
     * @param prefix 前缀
     */
    @NonNull
    public static String ensurePrefix(@NonNull String str, @NonNull String prefix) {
        Assert.hasText(str, "原字符串必须包含内容");
        Assert.hasText(prefix, "前缀字符串必须包含内容");

        return prefix + StringUtils.removeStart(str, prefix);
    }

    /**
     * 确保字符串包含指定后缀，如果已包含则不变，否则添加后缀
     *
     * @param str    源字符串
     * @param suffix 后缀
     */
    @NonNull
    public static String ensureSuffix(@NonNull String str, @NonNull String suffix) {
        Assert.hasText(str, "原字符串必须包含内容");
        Assert.hasText(suffix, "后缀字符串必须包含内容");

        return StringUtils.removeEnd(str, suffix) + suffix;
    }

    /**
     * 确保字符串被参数 bothFix 包围
     *
     * @param string 源字符串
     * @param bothFix 前后缀
     */
    @NonNull
    public static String ensureBoth(@NonNull String string, @NonNull String bothFix) {
        return ensureBoth(string, bothFix, bothFix);
    }

    /**
     * 确保字符串包含指定前后缀，如果已包含则不变，否则添加后缀
     *
     * @param string 源字符串
     * @param prefix 前缀
     * @param suffix 后缀
     */
    @NonNull
    public static String ensureBoth(@NonNull String string, @NonNull String prefix, @NonNull String suffix) {
        return ensureSuffix(ensurePrefix(string, prefix), suffix);
    }

    /**
     * 生成没有横线连接的 UUID
     */
    public static String randomUuidWithoutDash() {
        return StringUtils.remove(UUID.randomUUID().toString(), '-');
    }
}
