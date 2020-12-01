package work.cxlm.utils;

// import com.github.binarywang.java.emoji.EmojiConverter;

/**
 * 用于对 Emoji 字符进行编码解码的工具类
 * Created 2019/8/7 12:22
 *
 * @author Chiru
 * @deprecated 使用 hutool 提供的 EmojiUtil 替代
 */
@Deprecated
public class EmojiUtils {
//    private static final EmojiConverter CONVERTER = EmojiConverter.getInstance();  // 转化emoji工具类对象
//
//    /**
//     * 编码，将四字节字符转化为关键字形式，使用时需解码
//     */
//    public static String encode(String toEncode) {
//        if (toEncode == null) return null;
//        return CONVERTER.toAlias(toEncode);
//    }
//
//    /**
//     * 解码，将关键字形式转回Unicode字符串
//     */
//    public static String decode(String toDecode) {
//        return CONVERTER.toUnicode(toDecode);
//    }
//
//    /**
//     * 批量编码
//     */
//    public static String[] encode(String[] toEncode) {
//        if (toEncode == null) return null;
//        for (int i = 0, len = toEncode.length; i < len; i++) {
//            toEncode[i] = encode(toEncode[i]);
//        }
//        return toEncode;
//    }
//
//    /**
//     * 批量解码
//     */
//    public static String[] decode(String[] toDecode) {
//        if (toDecode == null) return null;
//        for (int i = 0, len = toDecode.length; i < len; i++) {
//            toDecode[i] = decode(toDecode[i]);
//        }
//        return toDecode;
//    }
}
