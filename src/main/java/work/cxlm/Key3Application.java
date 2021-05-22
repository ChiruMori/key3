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
        "work.cxlm.utils.spring",
        "work.cxlm.repository",
        "work.cxlm.service",
        "work.cxlm.security.filter",
        "work.cxlm.security.ott",
        "work.cxlm.core",
        "work.cxlm.controller",
        "work.cxlm.task",
})
public class Key3Application {

    public static void main(String[] args) {
        SpringApplication.run(Key3Application.class, args);
    }
}
