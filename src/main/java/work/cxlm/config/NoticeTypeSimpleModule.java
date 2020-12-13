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
import work.cxlm.model.enums.NoticeType;
import work.cxlm.model.enums.ValueEnum;

import java.io.IOException;

/**
 * NoticeType 转化模块，废弃该方案，使用 Integer 作为 NoticeType 的值类型更合理
 * created 2020/12/1 20:23
 *
 * @author Chiru
 */
@Deprecated
public class NoticeTypeSimpleModule extends SimpleModule {
    /*
    {
        addDeserializer(NoticeType.class, new StdDeserializer<NoticeType>(NoticeType.class) {
            @Override
            public NoticeType deserialize(JsonParser p, DeserializationContext
                    ctxt) throws IOException, JsonProcessingException {
                return ValueEnum.valueToEnum(NoticeType.class, p.getValueAsString());
            }
        });

        addSerializer(NoticeType.class, new StdSerializer<NoticeType>(NoticeType.class) {
            @Override
            public void serialize(NoticeType value, JsonGenerator gen, SerializerProvider provider) throws IOException {
                gen.writeString(value.getValue());
            }
        });
    }
     */
}
