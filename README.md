## MyFont

### 这是什么

这是一个沙雕项目，估计正常人应该用不到

**但是**， 如果你有一套只有自己能看懂的文字，而且规则也是使用奇怪的符号来替代汉字，这个程序可能适合你使用

使用本程序将可以不在苦恼字符的查询、更新、统计

生成电子图像也不再困难

### 提供的功能

+ 邮件通知功能

+ 基本的管理员用户功能（未完成）

+ 符号文字的增删改查（未完成）

+ 文本转化为密文（未完成）

+ 支持缺省字体，并提供管理（未完成）

+ 备份与恢复功能（未完成）

### 注意

+ 不提供发音的管理，暂时也并不考虑增加。~~毕竟只有自己能看懂的文字，发音有什么用呢~~

+ 暂不支持密文转文字。~~要是好做的话就做了~~

### 开发背景

自己有这么一套密文，但是使用时，常遇到记不清，且查询、更新麻烦的情况，于是想整这么个程序

前阶段在学习 **Halo** 的源码，受益匪浅，恰巧拿这个程序练手做一下

本不打算上传到 github 的，随着整个程序代码量的增长，还是决定拿出来，虽说应该没有其他人会用这个奇怪的东西。

~~但是在 Github 上面留下个奇怪的东西也挺有意思的（笑）~~

### 日志

**2020.10.27**

+ 开始、确定基本程序结构
+ 简单的程序设计
+ Email 功能完成

- - -

**2020.10.28**

+ 决定为本程序建立仓库
+ 日志、验证功能
+ 事件发布、订阅（后文不再记录事件）

- - -

**2020.11.1**

+ 缓存模块
+ 全局配置（MyFontProperties 部分功能）



### TODO

+ 基础架构
    + 缓存模块
        + 内存实现
    + 全局配置
+ 登录模块
    + AdminService
    + UserService
+ 重写邮件功能模块

### BUGS

+ Redis 缓存因未知问题（疑似配置问题）导致无法使用