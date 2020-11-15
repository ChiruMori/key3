package work.cxlm.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import freemarker.core.TemplateClassResolver;
import freemarker.template.TemplateException;
import freemarker.template.TemplateExceptionHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.jackson.JsonComponentModule;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.data.web.SortHandlerMethodArgumentResolver;
import org.springframework.format.FormatterRegistry;
import org.springframework.http.CacheControl;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.ViewResolverRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurationSupport;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;
import org.springframework.web.servlet.view.freemarker.FreeMarkerConfigurer;
import org.springframework.web.servlet.view.freemarker.FreeMarkerViewResolver;
import work.cxlm.model.enums.support.StringToEnumConverterFactory;
import work.cxlm.model.support.MyFontConst;
import work.cxlm.security.resolver.AuthenticationArgumentResolver;
import work.cxlm.utils.MyFontUtils;

import java.io.IOException;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.TimeUnit;

/**
 * created 2020/11/15 14:02
 *
 * @author ryanwang
 * @author Chiru
 */
@Slf4j
@Configuration
public class WebMvcAutoConfiguration implements WebMvcConfigurer {

    private static final String FILE_PROTOCOL = "file:///";

    private final PageableHandlerMethodArgumentResolver pageableResolver;

    private final SortHandlerMethodArgumentResolver sortResolver;

    private final MyFontProperties myFontProperties;

    public WebMvcAutoConfiguration(PageableHandlerMethodArgumentResolver pageableResolver,
                                   SortHandlerMethodArgumentResolver sortResolver,
                                   MyFontProperties myFontProperties) {
        this.pageableResolver = pageableResolver;
        this.sortResolver = sortResolver;
        this.myFontProperties = myFontProperties;
    }

    @Override
    public void extendMessageConverters(List<HttpMessageConverter<?>> converters) {
        converters.stream()
                .filter(c -> c instanceof MappingJackson2HttpMessageConverter)
                .findFirst()
                .ifPresent(converter -> {
                    // 添加自定义序列化模块
                    MappingJackson2HttpMessageConverter mappingJackson2HttpMessageConverter = (MappingJackson2HttpMessageConverter) converter;
                    Jackson2ObjectMapperBuilder builder = Jackson2ObjectMapperBuilder.json();
                    JsonComponentModule module = new JsonComponentModule();
                    module.addSerializer(PageImpl.class, new PageJacksonSerializer());  // 定制分页的 json 序列化行为
                    ObjectMapper objectMapper = builder.modules(module).build();
                    mappingJackson2HttpMessageConverter.setObjectMapper(objectMapper);
                });
    }

    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> resolvers) {
        resolvers.add(new AuthenticationArgumentResolver());  // 添加用户凭证校验解析器
        resolvers.add(pageableResolver);
        resolvers.add(sortResolver);
    }

    /**
     * 配置静态资源路径
     *
     * @param registry registry
     */
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        String workDir = FILE_PROTOCOL + MyFontUtils.ensureSuffix(myFontProperties.getWorkDir(), MyFontConst.FILE_SEPARATOR);

        // register /** resource handler.
        registry.addResourceHandler("/font/**")
                .addResourceLocations("classpath:/admin/")
                .addResourceLocations(workDir + "/static/");

        String uploadUrlPattern = MyFontUtils.ensureBoth(myFontProperties.getUploadUrlPrefix(), MyFontConst.URL_SEPARATOR) + "**";
        // String adminPathPattern = MyFontUtils.ensureSuffix(myFontProperties.getAdminPath(), MyFontConst.URL_SEPARATOR) + "**";

        registry.addResourceHandler(uploadUrlPattern)
                .setCacheControl(CacheControl.maxAge(7L, TimeUnit.DAYS))
                .addResourceLocations(workDir + "upload/");
        // registry.addResourceHandler(adminPathPattern)
        //        .addResourceLocations("classpath:/admin/");

        if (!myFontProperties.isDocDisabled()) {
            // 启用文档接口
            registry.addResourceHandler("/swagger-ui.html")
                    .addResourceLocations("classpath:/META-INF/resources/");
            registry.addResourceHandler("/webjars/**")
                    .addResourceLocations("classpath:/META-INF/resources/webjars/");
        }
    }


    @Override
    public void addFormatters(FormatterRegistry registry) {
        registry.addConverterFactory(new StringToEnumConverterFactory());  // 自定义 String 到 Enum 的转换器
    }

    /**
     * 配置 freemarker 模板路径
     *
     * @return FreeMarkerConfigurer 实例
     */
    @Bean
    public FreeMarkerConfigurer freemarkerConfig(MyFontProperties myFontProperties) throws IOException, TemplateException {
        FreeMarkerConfigurer configurer = new FreeMarkerConfigurer();
        configurer.setTemplateLoaderPaths(FILE_PROTOCOL + myFontProperties.getWorkDir() + "templates/", "classpath:/templates/");
        configurer.setDefaultEncoding("UTF-8");

        Properties properties = new Properties();
        // properties.setProperty("auto_import", "/common/macro/common_macro.ftl as common,/common/macro/global_macro.ftl as global");

        configurer.setFreemarkerSettings(properties);

        // 预定义配置
        freemarker.template.Configuration configuration = configurer.createConfiguration();
        configuration.setNewBuiltinClassResolver(TemplateClassResolver.SAFER_RESOLVER);
        if (myFontProperties.isProductionEnv()) {
            configuration.setTemplateExceptionHandler(TemplateExceptionHandler.RETHROW_HANDLER);
        }

        configurer.setConfiguration(configuration);

        return configurer;
    }

    /**
     * 配置视图解析器
     */
    @Override
    public void configureViewResolvers(ViewResolverRegistry registry) {
        FreeMarkerViewResolver resolver = new FreeMarkerViewResolver();
        resolver.setAllowRequestOverride(false);
        resolver.setCache(false);
        resolver.setExposeRequestAttributes(false);
        resolver.setExposeSessionAttributes(false);
        resolver.setExposeSpringMacroHelpers(true);
        resolver.setSuffix(MyFontConst.SUFFIX_FTL);
        resolver.setContentType("text/html; charset=UTF-8");
        registry.viewResolver(resolver);
    }

    @Bean
    public RequestMappingHandlerMapping createRequestMappingHandlerMapping() {
        return new MyFontRequestMappingHandlerMapping(myFontProperties);
    }

}
