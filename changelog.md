
### 日志

**2020.10.27**

+ 开始、确定基本程序结构
+ 简单的程序设计
+ Email 功能完成

- - -

**2020.10.28**

+ 决定为本程序建立仓库
+ 日志、验证功能
+ 事件发布、订阅

- - -

**2020.11.1**

+ 缓存模块
+ 全局配置（MyFontProperties 部分功能）

**2020.11.8~9**

+ 项目迁移
+ 缓存模块基本完成
+ 登录凭证、安全上下文
+ 全局配置项功能模块（及相关的工具类、实体类等）

**2020.11.13**

+ 全局配置项
+ 邮件功能模块重写

**2020.11.14~15**

+ AdminController
+ UserController
+ core 包、全局异常处理、全局 Controller 日志
+ 登录模块（未测试）含登录验证即相关部分
+ 系统安装、初始化模块（未测试）
+ 配置使用 Swagger2 API 文档（修正版本无法显示的 BUG）
+ 全局响应、全局异常处理、Json 序列化、全局 MVC 配置
+ 创建开发环境配置文件

- - -

**2020.11.16**

+ 剔除无关代码、重命名可复用的类

### TODO

+ 建库
+ 分功能模块开发
    + 用户模块（授权、绑定、取消授权、管理员）
    + 预约模块（预约、取消预约、关注时间段、活动室限制、签到限制）
    + 通知公告模块（管理员发公告、用户接受通知、预约可用通知、通知开关）
    + 社团功能模块（社团财务信息、社团活动室信息、社团账单）
    + 活动管理模块（管理员举办活动、用户参与活动、贡献点数核算等）
    + 后台管理模块（系统管理员与社团管理员使用的后台管理功能、提供系统参数设定、活动室相关参数设定等）
        + 权限分级：系统、社团管理员登录系统时，要求输入学工号，生成一次性动态口令发送到邮箱，用于登录系统
+ 测试增强
    + 补充全部模块的单元测试
    + 压力测试
+ 架构增强
    + 集群架构方案（SpringCloud, Docker）

### BUGS

+ Redis 缓存因未知问题（疑似配置问题）导致无法使用