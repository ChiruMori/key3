package work.cxlm.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;

/**
 * 程序相关配置
 * created 2020/11/1 17:06
 *
 * @author johnniang
 * @author cxlm
 */
@Configuration
@Slf4j
public class Key3Configuration {

    @Bean
    public ObjectMapper objectMapperBean(Jackson2ObjectMapperBuilder builder) {
        // 配置此项后，在遇到无法转换的对象时，不会抛出异常
        builder.failOnEmptyBeans(false);
        return builder.build();
    }
}
