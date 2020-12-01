package work.cxlm.utils;

import cn.hutool.extra.emoji.EmojiUtil;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * created 2020/11/17 11:30
 *
 * @author Chiru
 */
@Slf4j
public class EmojiUtilsTester {

    // 普通字符、特殊字符不受编码、解码影响
    // 注意，编码解码会修改原数组
    @Test
    public void encodeNormal() {
//        String[] src = new String[]{"String", "字符串", "!@#$%^", "↧ ↨ ⇄ ⇅", "▁▂▃▄"};
//        String[] res = new String[src.length];
//        for (int i = 0; i < src.length; i++) {
//            res[i] = EmojiUtils.encode(src[i]);
//        }
//        groupEquals(src, res);
//        res = EmojiUtils.decode(src);
//        groupEquals(src, res);
    }

    private void groupEquals(String[] src, String[] res) {
        for (int i = 0; i < src.length; i++) {
            assertEquals(src[i], res[i]);
        }
    }

    // 针对 Emoji 字符串进行编码，解码后可以恢复
    @Test
    public void encodeEmoji() {
//        String[] src = new String[]{"☀ ☁ ☔ ⛄ ⚡", "☺ \uD83D\uDE04 \uD83D\uDE22 \uD83D\uDE2D",
//                "❤ \uD83D\uDC94 \uD83D\uDC97 \uD83D\uDC93", "\uD83D\uDCE3 ⏳ ⌛ ⏰", "▶ \uD83D\uDD3C \uD83D\uDD3D ↩"};
//        String[] res = new String[src.length];
//        for (int i = 0; i < src.length; i++) {
//            res[i] = EmojiUtils.encode(src[i]);
//        }for (int i = 0; i < src.length; i++) {
//            log.debug("[{}] 编码后 -> [{}]", src[i], res[i]);
//        }
//        res = EmojiUtils.decode(src);
//        groupEquals(src, res);
    }

    @Test
    public void encodeEmojiByHutools() {
        String[] src = new String[]{"☀ ☁ ☔ ⛄ ⚡", "☺ \uD83D\uDE04 \uD83D\uDE22 \uD83D\uDE2D",
                "❤ \uD83D\uDC94 \uD83D\uDC97 \uD83D\uDC93", "\uD83D\uDCE3 ⏳ ⌛ ⏰", "▶ \uD83D\uDD3C \uD83D\uDD3D ↩"};
        String[] res = new String[src.length];
        for (int i = 0; i < src.length; i++) {
            res[i] = EmojiUtil.toAlias(src[i]);
        }
        for (int i = 0; i < src.length; i++) {
            log.debug("[{}] 编码后 -> [{}]", src[i], res[i]);
        }
        for (int i = 0; i < src.length; i++) {
            res[i] = EmojiUtil.toUnicode(src[i]);
        }
        for (int i = 0; i < src.length; i++) {
            log.debug("[{}] 解码后 -> [{}]", src[i], res[i]);
        }
    }
}
