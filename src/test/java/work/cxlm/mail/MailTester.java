package work.cxlm.mail;

import cn.hutool.core.lang.Assert;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import work.cxlm.service.MailService;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * @author cxlm
 * created 2020/10/19 16:30
 */
@SpringBootTest
public class MailTester {

    @Autowired
    MailService mailService;

    @Test
    public void testSendSampleEmail() throws InterruptedException {
        mailService.testConnection();
        mailService.sendTextMail("13190492195@163.com", "这是一封测试邮件", "测试邮件内容，简单文本邮件");
        Assert.isTrue(mailService instanceof AbstractMailService);
        AbstractMailService service = (AbstractMailService) mailService;
        final ExecutorService pool = service.getExecutorService();
        pool.awaitTermination(1, TimeUnit.MINUTES);
        System.out.println("邮件发送结束");
    }
}
