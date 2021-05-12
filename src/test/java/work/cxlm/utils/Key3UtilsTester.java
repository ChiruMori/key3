package work.cxlm.utils;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomUtils;
import org.junit.jupiter.api.Test;

import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * created 2020/10/28 17:19
 *
 * @author johnniang
 * @author ryanwang
 * @author cxlm
 */
@Slf4j
public class Key3UtilsTester {

    @Test
    void desensitizeSuccessTest() {
        String plainText = "12345678";

        String desensitization = Key3Utils.desensitize(plainText, 1, 1);
        assertEquals("1******8", desensitization);

        desensitization = Key3Utils.desensitize(plainText, 2, 3);
        assertEquals("12***678", desensitization);

        desensitization = Key3Utils.desensitize(plainText, 2, 6);
        assertEquals("12345678", desensitization);

        desensitization = Key3Utils.desensitize(plainText, 2, 7);
        assertEquals("12345678", desensitization);

        desensitization = Key3Utils.desensitize(plainText, 0, 0);
        assertEquals("********", desensitization);

        desensitization = Key3Utils.desensitize(plainText, -1, -1);
        assertEquals("********", desensitization);

        desensitization = Key3Utils.desensitize(plainText, -1, 1);
        assertEquals("*******8", desensitization);
    }

    @Test
    void desensitizeFailureTest() {
        String plainText = " ";
        assertThrows(IllegalArgumentException.class, () -> Key3Utils.desensitize(plainText, 1, 1));
    }

    @Test
    void timeFormatTest() {
        long seconds = 0;
        String timeFormat = Key3Utils.timeFormat(seconds);
        assertEquals("0 秒", timeFormat);

        seconds = -1;
        timeFormat = Key3Utils.timeFormat(seconds);
        assertEquals("0 秒", timeFormat);

        seconds = 30;
        timeFormat = Key3Utils.timeFormat(seconds);
        assertEquals("30 秒", timeFormat);

        seconds = 60;
        timeFormat = Key3Utils.timeFormat(seconds);
        assertEquals("1 分", timeFormat);

        seconds = 120;
        timeFormat = Key3Utils.timeFormat(seconds);
        assertEquals("2 分", timeFormat);

        seconds = 3600;
        timeFormat = Key3Utils.timeFormat(seconds);
        assertEquals("1 时", timeFormat);

        seconds = 7200;
        timeFormat = Key3Utils.timeFormat(seconds);
        assertEquals("2 时", timeFormat);

        seconds = 7200 + 30;
        timeFormat = Key3Utils.timeFormat(seconds);
        assertEquals("2 时, 30 秒", timeFormat);

        seconds = 7200 + 60 + 30;
        timeFormat = Key3Utils.timeFormat(seconds);
        assertEquals("2 时, 1 分, 30 秒", timeFormat);
    }

    @Test
    void pluralizeTest() {

        String label = "chance";
        String pluralLabel = "chances";

        String pluralizedFormat = Key3Utils.pluralize(1, label, pluralLabel);
        assertEquals("1 chance", pluralizedFormat);


        pluralizedFormat = Key3Utils.pluralize(2, label, pluralLabel);
        assertEquals("2 chances", pluralizedFormat);

        pluralizedFormat = Key3Utils.pluralize(0, label, pluralLabel);
        assertEquals("no chances", pluralizedFormat);

        // Test random positive time
        IntStream.range(0, 10000).forEach(i -> {
            long time = RandomUtils.nextLong(2, Long.MAX_VALUE);
            String result = Key3Utils.pluralize(time, label, pluralLabel);
            assertEquals(time + " " + pluralLabel, result);
        });

        // Test random negative time
        IntStream.range(0, 10000).forEach(i -> {
            long time = (-1) * RandomUtils.nextLong();
            String result = Key3Utils.pluralize(time, label, pluralLabel);
            assertEquals("no " + pluralLabel, result);
        });
    }

    @Test
    @SuppressWarnings("all")
    void pluralizeLabelExceptionTest() {
        assertThrows(IllegalArgumentException.class, () -> Key3Utils.pluralize(1, null, null));
    }

    @Test
    void textEnsurePrefixAndSuffix() {
        assertEquals("abcd", Key3Utils.ensurePrefix("abcd", "ab"));
        assertEquals("abcd", Key3Utils.ensurePrefix("abcd", "abcd"));
        assertEquals("abcd", Key3Utils.ensurePrefix("cd", "ab"));
        assertEquals("accd", Key3Utils.ensurePrefix("cd", "ac"));

        assertEquals("accd", Key3Utils.ensureSuffix("ac", "cd"));
        assertEquals("accd", Key3Utils.ensureSuffix("accd", "cd"));
        assertEquals("accd", Key3Utils.ensureSuffix("accd", "accd"));
    }

}
