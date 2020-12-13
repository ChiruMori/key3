package work.cxlm.mail;

import freemarker.template.Template;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationListener;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;
import org.springframework.ui.freemarker.FreeMarkerTemplateUtils;
import org.springframework.web.servlet.view.freemarker.FreeMarkerConfig;
import work.cxlm.event.OptionUpdatedEvent;
import work.cxlm.service.OptionService;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;

/**
 * created 2020/11/13 17:40
 *
 * @author Chiru
 * @author ryanwang
 * @author johnniang
 */
@Slf4j
@Service
public class MailServiceImpl extends AbstractMailService implements ApplicationListener<OptionUpdatedEvent> {

    private final FreeMarkerConfig freeMarkerConfig;

    protected MailServiceImpl(FreeMarkerConfig freeMarkerConfig, OptionService optionService) {
        super(optionService);
        this.freeMarkerConfig = freeMarkerConfig;
    }

    @Override
    public void sendTextMail(String to, String subject, String content) {
        sendMailTemplate(true, messageHelper -> {
            messageHelper.setSubject(subject);
            messageHelper.setTo(to);
            messageHelper.setText(content);
        });
    }

    @Override
    public void sendTemplateMail(String to, String subject, Map<String, Object> content, String templateName) {
        sendMailTemplate(true, msgHelper -> {
            // 使用 FreeMarker 构建邮件内容并发送
            Template mailTemplate = freeMarkerConfig.getConfiguration().getTemplate(templateName);
            String emailContent = FreeMarkerTemplateUtils.processTemplateIntoString(mailTemplate, content);

            msgHelper.setText(emailContent, true);
            msgHelper.setSubject(subject);
            msgHelper.setTo(to);
        });
    }

    @Override
    public void sendAttachMail(String to, String subject, Map<String, Object> content, String templateName, String attachFilePath) {
        sendMailTemplate(true, msgHelper -> {
            msgHelper.setTo(to);
            msgHelper.setSubject(subject);
            Path attachPath = Paths.get(attachFilePath);
            msgHelper.addAttachment(attachPath.getFileName().toString(), attachPath.toFile());
        });
    }

    @Override
    public void onApplicationEvent(@NonNull OptionUpdatedEvent event) {
        clearCache();
    }
}
