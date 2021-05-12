package work.cxlm.task;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import work.cxlm.model.entity.User;
import work.cxlm.model.enums.UserGender;
import work.cxlm.model.enums.UserRole;
import work.cxlm.model.support.Key3Const;
import work.cxlm.service.UserService;

/**
 * created 2020/12/17 14:36
 *
 * @author Chiru
 */
@Component
@Slf4j
public class InitialCommandLineRunner implements CommandLineRunner {

    private final UserService userService;

    public InitialCommandLineRunner(UserService userService) {
        this.userService = userService;
    }

    @Override
    public void run(String... args) {
        // 检测并生成系统核心用户
        User systemCoreUser = userService.getByIdOfNullable(-1);
        if (systemCoreUser == null) {
            systemCoreUser = new User();
            systemCoreUser.setRealName("系统");
            systemCoreUser.setHead(Key3Const.SYSTEM_HEAD);
            systemCoreUser.setId(-1);
            systemCoreUser.setStudentNo(-1L);
            systemCoreUser.setReceiveMsg(false);
            systemCoreUser.setEmail("qfzs@cxlm.work");
            systemCoreUser.setGender(UserGender.UNKNOWN);
            systemCoreUser.setSign("这个是系统核心用户，如果误删请重启程序！");
            systemCoreUser.setRole(UserRole.SYSTEM_ADMIN);
            userService.create(systemCoreUser);
            log.info("系统核心用户已自动生成");
        } else {
            log.info("系统核心用户检测通过");
        }
    }
}
