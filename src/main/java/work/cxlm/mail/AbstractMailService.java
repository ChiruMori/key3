package work.cxlm.mail;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.util.Assert;
import work.cxlm.exception.EmailException;
import work.cxlm.model.properties.EmailProperties;
import work.cxlm.service.OptionService;

import javax.mail.MessagingException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.io.UnsupportedEncodingException;
import java.util.Arrays;
import java.util.concurrent.*;

/**
 * Abstract mail service.
 * created 2020.11.09 14:55:03
 *
 * @author johnniang
 * @author cxlm
 */
@Slf4j
public abstract class AbstractMailService implements MailService {

    private static final int DEFAULT_POOL_SIZE = 5;
    private JavaMailSender sender;
    private MailProperties mailProperties;
    private String fromName;
    @Nullable
    private ExecutorService executorService;

    protected final OptionService optionService;

    protected AbstractMailService(OptionService optionService) {
        this.optionService = optionService;
    }

    @NonNull
    public ExecutorService getExecutorService() {
        if (this.executorService == null) {
            this.executorService = new ThreadPoolExecutor(DEFAULT_POOL_SIZE, DEFAULT_POOL_SIZE, 0,
                    TimeUnit.SECONDS, new LinkedBlockingQueue<>(), t -> new Thread(t, "邮件发送线程"));
        }
        return executorService;
    }

    public void setExecutorService(@Nullable ExecutorService executorService) {
        this.executorService = executorService;
    }

    /**
     * 获得 MailSender
     */
    @NonNull
    private synchronized JavaMailSender getMailSender() {
        if (sender == null) {
            // 设置 MailSender
            sender = MailSenderFactory.getMailSender(getMailProperties());
        }

        return sender;
    }

    /**
     * 测试 Email 服务器
     */
    @Override
    public void testConnection() {
        JavaMailSender mailSender = getMailSender();
        if (mailSender instanceof JavaMailSenderImpl) {
            JavaMailSenderImpl javaMailSender = (JavaMailSenderImpl) mailSender;
            try {
                javaMailSender.testConnection();
            } catch (MessagingException e) {
                throw new EmailException("无法连接到邮箱服务器，请检查邮箱配置.[" + e.getMessage() + "]", e);
            }
        }
    }

    /**
     * 发送模板邮件，需要同时传递回调函数
     */
    protected void sendMailTemplate(@Nullable Callback callback) {
        if (callback == null) {
            log.info("回调函数为空，终止发送");
            return;
        }
        JavaMailSender mailSender = getMailSender();
        // 配置参数
        MimeMessageHelper messageHelper = new MimeMessageHelper(sender.createMimeMessage());
        try {
            messageHelper.setFrom(getFromAddress(sender));
            callback.handle(messageHelper);
            MimeMessage mimeMessage = messageHelper.getMimeMessage();
            mailSender.send(mimeMessage);

            log.info("发送邮件到 [{}] 成功, 主题: [{}], 数据: [{}]",
                    Arrays.toString(mimeMessage.getAllRecipients()),
                    mimeMessage.getSubject(),
                    mimeMessage.getSentDate());
        } catch (Exception e) {
            throw new EmailException("邮件发送失败，请检查 SMTP 服务配置是否正确", e);
        }
    }

    /**
     * 获取发件地址
     */
    private synchronized InternetAddress getFromAddress(@NonNull JavaMailSender sender) throws UnsupportedEncodingException {
        Assert.notNull(sender, "Sender 不能为 null");

        if (StringUtils.isBlank(fromName)) {
            fromName = optionService.getByPropertyOfNonNull(EmailProperties.FROM_NAME).toString();
        }

        if (sender instanceof JavaMailSenderImpl) {
            // 获得用户名
            JavaMailSenderImpl mailSender = (JavaMailSenderImpl) sender;
            return new InternetAddress(mailSender.getUsername(), fromName, mailSender.getDefaultEncoding());
        }
        throw new UnsupportedOperationException("不支持的 Sender: " + sender.getClass().getName());
    }

    /**
     * 发送模板邮件
     *
     * @param callback   callback message handler
     * @param tryToAsync 是否使用线程池（并行发送）
     */
    protected void sendMailTemplate(boolean tryToAsync, @Nullable Callback callback) {
        ExecutorService executorService = getExecutorService();
        if (tryToAsync) {
            // 异步发送
            executorService.execute(() -> sendMailTemplate(callback));
        } else {
            // 同步发送
            sendMailTemplate(callback);
        }
    }

    /**
     * 获得邮件参数
     */
    @NonNull
    private synchronized MailProperties getMailProperties() {
        if (mailProperties == null) {
            MailProperties properties = new MailProperties(log.isDebugEnabled());
            properties.setHost(optionService.getByPropertyOrDefault(EmailProperties.HOST, String.class));
            properties.setPort(optionService.getByPropertyOrDefault(EmailProperties.SSL_PORT, Integer.class));
            properties.setUsername(optionService.getByPropertyOrDefault(EmailProperties.USERNAME, String.class));
            properties.setPassword(optionService.getByPropertyOrDefault(EmailProperties.PASSWORD, String.class));
            properties.setProtocol(optionService.getByPropertyOrDefault(EmailProperties.PROTOCOL, String.class));
            mailProperties = properties;
        }
        return mailProperties;
    }

    /**
     * 列出全部邮箱配置
     */
    protected void clearCache() {
        sender = null;
        fromName = null;
        mailProperties = null;
        log.debug("清除全部的 Mail 缓存项");
    }

    /**
     * 消息回调接口
     */
    protected interface Callback {
        /**
         * 处理消息
         *
         * @param messageHelper 消息构建器
         * @throws Exception 任何异常
         */
        void handle(@NonNull MimeMessageHelper messageHelper) throws Exception;
    }
}
