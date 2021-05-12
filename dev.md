## 系统基础模块

### 系统基础功能增强

> 全局事件监听用于将业务无关的部分处理抽离出来，以降低耦合，增加可读性

+ 全局事件监听模块
    + work.cxlm.listener 使用 Spring 提供的事件分发机制，该包下所有类均为程序事件的监听处理，注入 Bean
    + 任何地方，使用 Spring 提供的 EventPublisher 生产事件，即可被上述类包中对应的方法捕获进行处理

> 全局日志处理为系统管理员模块的子模块。注意此模块与系统控制台上输出的分级日志不是同一个东西。    

+ 全局日志处理
    + 相关代码：针对 Log 实体的各层处理、日志事件分发与监听
    + 处理逻辑：在程序的任何地方，遇到需要进行日志记录的情况，就通过 eventPublisher 发送对应类型的日志。日志监听器捕获对应事件后，调用 service 层将日志实体写入持久层

> 全局配置模块通常用于在全局注入系统中通用的 Bean，或者规定部分程序参数 

+ 相关代码：work.cxlm.config
    + PageJacksonSerializer 自定义的 Json 处理器
    + QfzsConfiguration 注入了缓存 Bean 以及 Json 处理相关 Bean 的 Builder
    + QfzsRequestMappingHandlerMapping 自定义拦截器，对静态资源的请求进行放行
    + WebMvcAutoConfiguration 注入了与 Web 相关处理的 Bean，以及一些基础配置，具体包含内容如下
        + Json 处理（这可以影响默认的 Json 处理）
        + 后文的参数解析器
        + 静态资源路径的绑定
        + String 到 Enum 的转换器
        + freemarker 的静态资源相关配置
        + 上文的自定义拦截器
    + SwaggerConfiguration 自动 API 接口文档生成（Swagger）的相关配置
    + QfzsProperties 通过配置文件可以读入的配置项
    
> 针对常规编程的优化

+ 自定义异常，封装基础的异常，以更可读的方式使用异常。位置：work.cxlm.exception
+ 系统工具包通常为可复用、可抽离出来的，与系统耦合度较低的代码。位置：含 utils 的各个类包下的所有类

### 针对 MVC 分层设计的优化

> 这些优化是基于常规 MVC 开发进行的，针对常规开发时通用的行为、方法进行封装以提高开发效率。

+ Repository (work.cxlm.repository)
    + BaseRepository 对主键的集合操作相关方法进行了声明，不生成 Bean
+ Service (work.cxlm.service.base)
    + CrudService 声明与数据库交互、完成常规 CRUD 操作的 service 通用方法。其中泛型 `DOMAIN`、`ID` 分别表示操作的实体类、实体主键类型
    + AbstractCrudService 实现上述接口的全部方法，需要子类继承时传入 repository 即可使用
    + AbstractCacheCrudService 继承上述接口，实现一个弱引用缓存，缓存全部实体的 Map
+ Controller (作用目标 work.cxlm.controller)
    + work.cxlm.annotation
        + DisableOnCondition 注解及相关切面（aspect）、解析器，作用于 Controller 方法上，可以限制在某些情况下（通常为系统配置的调整）禁用 API
        + WxMiniUserUser 注解及相关切面（aspect）、解析器，之后应该会弃用，作用于 Controller 的参数，解析请求中的 WxMiniUser 在缓存的配合下还原用户登录凭证。Session 方案的优化，因系统默认 Session 受小程序影响无法正常使用而引入的方法
    + work.cxlm.core
        + CommonResultControllerAdvice 针对全局的 Http 响应进行的封装处理
        + ControllerExceptionHandler 针对 Controller 中抛出的异常进行的统一处理，通常为设置状态码，设置更友好的提示信息
        + ControllerLogAop 针对所有 Controller 方法的切面，仅用于测试时的控制台日志，在开发环境下使用

### 针对实体类的处理

> DTO 对象为数据传输对象，通常为 entity 的字段封装，用于在程序中传递数据。 

+ work.cxlm.model.dto
    + base.InputConverter 通常为后文的 param 包中实体使用，将一个数据传输对象转化为 entity，用于从外部向程序传递数据
    + base.OutputConverter 为 dto 和 vo 使用，将 entity 转化为数据传输对象，用于向程序外传递数据
    
> entity 表示实体类，通常与数据库表直接关联

+ work.cxlm.model.entity
    + id 实体类中使用的联合主键
    + support 提供了自定义的 ID 生成策略
    + BaseEntity 基础字段创建日期、修改日期的修改，不映射为单独的表

> 系统中各类使用的枚举类，通常为实体类使用

+ work.cxlm.model.enums
    + converter 提供了与实体相关枚举类的转换方法
    + support.StringToEnumConverterFactor 转化类工厂，传入目标枚举类的类对象，可以得到转化对象。用于将字符串转化为对应的枚举类
    + ValueEnum 需要映射为值的枚举类接口
    
> params 为输入行数据传输对象，通常通过一个表单生成，为 InputConverter 的实现类。在传入系统时，系统通常会对该对象的字段进行校验

+ work.cxlm.model.params

> vo 为传输到前端显示的数据传输对象

+ work.cxlm.model.vo

> properties 为一组系统配置项，使用枚举类实现

+ work.cxlm.model.properties
    + PropertyEnum 为所有子类提供了与字符串转化的方法，为 ValueEnum 的子接口
    
> support 提供一些与实体相关的，无法分组的功能

+ BaseResponse 公共响应的封装实体类
+ CreateCheck, UpdateCheck 接口，针对 param 进行的分组，这两个接口不提供任何功能，只在验证字段时，提供分组提示功能
+ QfzsConst 系统需要的全局静态变量

## 缓存模块

> 缓存模块属于较为基础的模块，被多个模块使用，使用键值方式进行缓存，通常用于信息的暂存或者提高访问速度

+ 主要代码：work.cxlm.cache
    + cache 提供了 @CacheLock 注解的支持，用于方法上，在缓存级对该方法加方法锁。即通过在缓存中设置一个标记，防止对方法的并发调用
    + CacheStore 声明缓存的基本方法
    + CacheWrapper 缓存值的包装类，封装了过期、创建时间属性
    + AbstractCacheStore 声明了任意类型键值的缓存，并提供部分实现
    + AbstractStringCacheStore 字符串类型键值的缓存，并提供类型转换相关的方法，使其可以操作支持序列化的任何对象
    + InMemoryCacheStore, LevelCacheStore, RedisCacheStore 均为 AbstractStringCacheStore 的子类，分别为内存实现、Level DB 实现、Redis 集群实现。通过调整系统配置（Key3Configuration）可以选择使用不同的实现

## 用户验证模块

> 本系统采用的用户登录凭证方案为 ThreadLocal 存储，而不是默认的 Session 方案，小程序会干扰 Session 的使用，且 ThreadLocal 方案相比 Session 方案耦合度更低。但因未被原生框架支持，需要手动过滤请求，完成参数的解析注入以及用户登录凭证的存储、获取

+ 相关类：work.cxlm.security
    + support.UserDetail 封装一个用户实体
    + authentication.Authentication 封装 UserDetail 实体，表示用户登录凭证
    + context
        + SecurityContext, SecurityContextImpl 封装 Authentication 表示用户的登录凭证，这个是实际使用的登录凭证
        + SecurityContextHolder 封装了从 ThreadLocal 中取得、设置 SecurityContext 的方法
    + filter 系统过滤器
        + AbstractAuthenticationFilter 系统过滤器的默认实现
        + ContentFilter 针对部分接口进行放行的过滤器，默认过滤器
        + WxMiniAuthenticationFilter 针对微信小程序用户的登录凭证过滤器
    + handler 用户授权验证失败后调用的方法
    + ott OneTimeToken 的 Service，OneTimeToken 为部分关键接口的安全保障，绑定一个 URL ，仅能使用一次，目前系统中未使用该方式对接口进行处理
    + WxMiniUserUserParameterResolver 参数解析器，为 @WxMiniUserUser 注解的解释器，从 SecurityContext 中取得合法用户凭证
    + AuthenticationArgumentResolver 参数解析器，针对所有的请求调用上述 Holder 获取登录凭证，如果不存在凭证或凭证失效则抛出异常。如果有效，则从参数中将该授权信息解析出来
    + token.AuthToken 用户登录后得到的凭证

## 零碎的模块

### Email 模块

> Email 模块用于在系统发生一些需要报告的事项时（比如遭遇未处理的异常），向系统管理员进行通知。也可用于普通用户的通知（未确定，可选）

+ 相关类：work.cxlm.mail
    + MailProperties 拓展了 spring 默认邮箱配置，以支持自定义配置
    + MailSenderFactory 为 JavaMailSender 的简单工厂，通过置顶配置构造对象
    + MailService 接口定义了邮件发送的关键方法
    + AbstractMailService 封装了发送邮件相关处理，将与发件内容相关的处理暴露在一个函数式接口（Callback.handle()）中，方便子类调用
    + MailServiceImpl 实现了不同内容邮件的发送，将接口实例注入到容器
    
### RPC 模块

> RPC 模块用于在后端发送 HTTP 请求

+ 主要代码：work.cxlm.rpc
    + 子包：每个包表示请求一个接口使用的参数与响应实体
    + GetParam：提供了 Get 请求参数的转化方法
    + RpcClient：发送 Http 请求的静态工具类
    
### task 自动任务模块

+ InitialCommandLineRunner 系统启动自检
+ RoomRefreshTask 用于发送活动室重置通知
+ TimeNoticeTask 用于发送预约时段开始通知 
