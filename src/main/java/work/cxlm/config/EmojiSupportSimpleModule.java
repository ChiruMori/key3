package work.cxlm.config;

import cn.hutool.extra.emoji.EmojiUtil;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;

import java.io.IOException;

/**
 * Emoji 转化模块，用在 ObjectMapper 中
 * created 2020/12/1 20:23
 *
 * @author Chiru
 */
public class EmojiSupportSimpleModule extends SimpleModule {
    {
        addDeserializer(String.class, new StdDeserializer<String>(String.class) {
            @Override
            public String deserialize(JsonParser p, DeserializationContext
                    ctxt) throws IOException, JsonProcessingException {
                return EmojiUtil.toAlias(p.getValueAsString());
            }
        });

        addSerializer(String.class, new StdSerializer<String>(String.class) {
            @Override
            public void serialize(String value, JsonGenerator gen, SerializerProvider provider) throws IOException {
                gen.writeString(EmojiUtil.toUnicode(value));
            }
        });
    }
}
