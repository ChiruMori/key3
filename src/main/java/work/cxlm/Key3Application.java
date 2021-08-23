package work.cxlm;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

/**
 * @author cxlm
 * created 2020/10/16 23:08
 */
@SpringBootApplication
@ComponentScan(basePackages = {
        "work.cxlm.utils.spring",  // 静态 bean 管理器所在包
        "work.cxlm.repository",  // 存储层
        "work.cxlm.service",  // 业务逻辑层
        "work.cxlm.security.filter",  // web 请求过滤器
        "work.cxlm.security.ott",  // 一次性 token 业务逻辑
        "work.cxlm.core",  // 系统核心组件
        "work.cxlm.controller",  // 系统控制器
        "work.cxlm.task",  // 自动化跑批
})
public class Key3Application {

    public static void main(String[] args) {
        SpringApplication.run(Key3Application.class, args);
    }
}
