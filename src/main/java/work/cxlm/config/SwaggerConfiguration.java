package work.cxlm.config;

import com.fasterxml.classmate.TypeResolver;
import io.swagger.models.auth.In;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.lang.NonNull;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.RequestMethod;
import springfox.documentation.builders.*;
import springfox.documentation.schema.AlternateTypeRule;
import springfox.documentation.schema.AlternateTypeRuleConvention;
import springfox.documentation.service.*;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spi.service.contexts.SecurityContext;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger.web.SecurityConfiguration;
import springfox.documentation.swagger.web.SecurityConfigurationBuilder;
import springfox.documentation.swagger2.annotations.EnableSwagger2;
import work.cxlm.model.entity.User;
import work.cxlm.model.support.Key3Const;
import work.cxlm.security.support.UserDetail;

import java.lang.reflect.Type;
import java.time.temporal.Temporal;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static springfox.documentation.schema.AlternateTypeRules.newRule;

/**
 * Swagger 配置类
 * created 2020/11/15 15:20
 *
 * @author johnniang
 * @author Chiru
 */
@EnableSwagger2
@Configuration
@Slf4j
public class SwaggerConfiguration {

    private final Key3Properties key3Properties;

    private final List<ResponseMessage> globalResponses = Arrays.asList(
            new ResponseMessageBuilder().code(200).message("Success").build(),
            new ResponseMessageBuilder().code(400).message("Bad request").build(),
            new ResponseMessageBuilder().code(401).message("Unauthorized").build(),
            new ResponseMessageBuilder().code(403).message("Forbidden").build(),
            new ResponseMessageBuilder().code(404).message("Not found").build(),
            new ResponseMessageBuilder().code(500).message("Internal server error").build());

    public SwaggerConfiguration(Key3Properties key3Properties) {
        this.key3Properties = key3Properties;
    }

    /**
     * 非管理员功能 API 文档
     */
    @Bean
    public Docket key3DefaultDocket() {
        if (key3Properties.isDocDisabled()) {
            log.debug("文档未开启");
        }

        return buildApiDocket("用户功能模块 API",
                "work.cxlm.controller.mini.api",
                "/key3/users/api/**")
                .securitySchemes(contentApiKeys())
                .securityContexts(contentSecurityContext())
                .enable(!key3Properties.isDocDisabled());
    }

    /**
     * 管理员功能 API 文档
     */
    @Bean
    public Docket key3AdminApi() {
        if (key3Properties.isDocDisabled()) {
            log.debug("文档未开启");
        }

        return buildApiDocket("管理员功能 API 文档",
                "work.cxlm.controller.admin.api",
                "/key3/admin/api/**")
                .securitySchemes(adminApiKeys())
                .securityContexts(adminSecurityContext())
                .enable(!key3Properties.isDocDisabled());
    }

    /**
     * 管理员功能 API 文档
     */
    @Bean
    public Docket key3TimeApi() {
        if (key3Properties.isDocDisabled()) {
            log.debug("文档未开启");
        }

        return buildApiDocket("时段相关 API 文档",
                "work.cxlm.controller.mini.api",
                "/key3/time/api/**")
                .securitySchemes(adminApiKeys())
                .securityContexts(adminSecurityContext())
                .enable(!key3Properties.isDocDisabled());
    }

    @Bean
    SecurityConfiguration security() {
        return SecurityConfigurationBuilder.builder()
                .clientId("key3-client-id")
                .clientSecret("key3-client-secret")
                .realm("key3-realm")
                .appName("key3-app")
                .scopeSeparator(",")
                .additionalQueryStringParams(null)
                .useBasicAuthenticationWithAccessCodeGrant(false)
                .build();
    }

    private Docket buildApiDocket(@NonNull String groupName, @NonNull String basePackage, @NonNull String antPattern) {
        Assert.hasText(groupName, "组名不能为空");
        Assert.hasText(basePackage, "包名不能为空");
        Assert.hasText(antPattern, "Ant pattern 不能为空");

        return new Docket(DocumentationType.SWAGGER_2)
                .groupName(groupName)
                .select()
                .apis(RequestHandlerSelectors.basePackage(basePackage))
                .paths(PathSelectors.ant(antPattern))
                .build()
                .apiInfo(apiInfo())
                .useDefaultResponseMessages(false)
                .globalResponseMessage(RequestMethod.GET, globalResponses)
                .globalResponseMessage(RequestMethod.POST, globalResponses)
                .globalResponseMessage(RequestMethod.DELETE, globalResponses)
                .globalResponseMessage(RequestMethod.PUT, globalResponses)
                .directModelSubstitute(Temporal.class, String.class);
    }

    private List<ApiKey> adminApiKeys() {
        return Arrays.asList(
                new ApiKey("Token from header", Key3Const.ADMIN_TOKEN_HEADER_NAME, In.HEADER.name()),
                new ApiKey("Token from query", Key3Const.ADMIN_TOKEN_QUERY_NAME, In.QUERY.name())
        );
    }

    private List<SecurityContext> adminSecurityContext() {
        return Collections.singletonList(
                SecurityContext.builder()
                        .securityReferences(defaultAuth())
                        .forPaths(PathSelectors.regex("/font/api/admin/.*"))
                        .build()
        );
    }

    private List<ApiKey> contentApiKeys() {
        return Arrays.asList(
                new ApiKey("Access key from header", Key3Const.API_ACCESS_KEY_HEADER_NAME, In.HEADER.name()),
                new ApiKey("Access key from query", Key3Const.API_ACCESS_KEY_QUERY_NAME, In.QUERY.name())
        );
    }

    private List<SecurityContext> contentSecurityContext() {
        return Collections.singletonList(
                SecurityContext.builder()
                        .securityReferences(contentApiAuth())
                        .forPaths(PathSelectors.regex("/font/api/content/.*"))
                        .build()
        );
    }

    private List<SecurityReference> defaultAuth() {
        AuthorizationScope[] authorizationScopes = {new AuthorizationScope("Admin api", "Access admin api")};
        return Arrays.asList(new SecurityReference("Token from header", authorizationScopes),
                new SecurityReference("Token from query", authorizationScopes));
    }

    private List<SecurityReference> contentApiAuth() {
        AuthorizationScope[] authorizationScopes = {new AuthorizationScope("content api", "Access content api")};
        return Arrays.asList(new SecurityReference("Access key from header", authorizationScopes),
                new SecurityReference("Access key from query", authorizationScopes));
    }

    private ApiInfo apiInfo() {
        return new ApiInfoBuilder()
                .title("Key3 API 文档")
                .description("描述程序后端接口的 API 文档")
                .version(key3Properties.getVersion())
                .termsOfServiceUrl("https://github.com/first-snow/myfont")
                .contact(new Contact("myfont", "https://github.com/first-snow/myfont/issues", "cxlm@cxlm.work"))
                .license("GNU General Public License v3.0")
                .licenseUrl("https://github.com/first-snow/myfont/blob/master/LICENSE")
                .build();
    }

    @Bean
    public AlternateTypeRuleConvention customizeConvention(TypeResolver resolver) {
        return new AlternateTypeRuleConvention() {
            @Override
            public int getOrder() {
                return Ordered.LOWEST_PRECEDENCE;
            }

            @Override
            public List<AlternateTypeRule> rules() {
                return Arrays.asList(
                        newRule(User.class, emptyMixin(User.class)),
                        newRule(UserDetail.class, emptyMixin(UserDetail.class)),
                        newRule(resolver.resolve(Pageable.class), resolver.resolve(pageableMixin())),
                        newRule(resolver.resolve(Sort.class), resolver.resolve(sortMixin())));
            }
        };
    }

    /**
     * Controller 方法参数 (eg: HttpServletRequest, ModelView ...).
     *
     * @param clazz 参数类型，不能为 null
     * @return 空类型
     */
    private Type emptyMixin(Class<?> clazz) {
        Assert.notNull(clazz, "参数类型不能为 null");

        return new AlternateTypeBuilder()
                .fullyQualifiedClassName(String.format("%s.generated.%s", clazz.getPackage().getName(), clazz.getSimpleName()))
                .withProperties(Collections.emptyList())
                .build();
    }

    private Type sortMixin() {
        return new AlternateTypeBuilder()
                .fullyQualifiedClassName(String.format("%s.generated.%s", Sort.class.getPackage().getName(), Sort.class.getSimpleName()))
                .withProperties(Collections.singletonList(property(String[].class, "sort")))
                .build();
    }

    private Type pageableMixin() {
        return new AlternateTypeBuilder()
                .fullyQualifiedClassName(String.format("%s.generated.%s", Pageable.class.getPackage().getName(), Pageable.class.getSimpleName()))
                .withProperties(Arrays.asList(property(Integer.class, "page"), property(Integer.class, "size"), property(String[].class, "sort")))
                .build();
    }

    private AlternateTypePropertyBuilder property(Class<?> type, String name) {
        return new AlternateTypePropertyBuilder()
                .withName(name)
                .withType(type)
                .withCanRead(true)
                .withCanWrite(true);
    }

}
