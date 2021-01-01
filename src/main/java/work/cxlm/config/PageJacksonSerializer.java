package work.cxlm.config;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import org.springframework.data.domain.Page;

import java.io.IOException;

/**
 * 自定义的分页 (Page 实例的) json 序列化工具
 * created 2020/11/15 15:12
 *
 * @author johnniang
 * @author Chiru
 */
@SuppressWarnings("rawtypes")
public class PageJacksonSerializer extends JsonSerializer<Page> {

    @Override
    public void serialize(Page page, JsonGenerator gen, SerializerProvider serializers) throws IOException {
        gen.writeStartObject();

        gen.writeObjectField("content", page.getContent());
        gen.writeNumberField("pages", page.getTotalPages());
        gen.writeNumberField("total", page.getTotalElements());
        gen.writeNumberField("page", page.getNumber());
        gen.writeNumberField("rpp", page.getSize());
        gen.writeBooleanField("hasNext", page.hasNext());
        gen.writeBooleanField("hasPrevious", page.hasPrevious());
        gen.writeBooleanField("isFirst", page.isFirst());
        gen.writeBooleanField("isLast", page.isLast());
        gen.writeBooleanField("isEmpty", page.isEmpty());
        gen.writeBooleanField("hasContent", page.hasContent());
        gen.writeEndObject();
    }
}
