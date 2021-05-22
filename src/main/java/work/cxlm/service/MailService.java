package work.cxlm.service;

import java.util.Map;

/**
 * Mail service interface.
 *
 * @author Chiru
 * @author ryanwang
 */
public interface MailService {

    /**
     * 发送纯文本邮件
     *
     * @param to      接受者
     * @param subject 主题
     * @param content 邮件内容
     */
    void sendTextMail(String to, String subject, String content);

    /**
     * 使用 Freemarker 发送模板消息
     *
     * @param to           接受者
     * @param subject      主题
     * @param content      邮件内容
     * @param templateName freemarker 模板名
     */
    void sendTemplateMail(String to, String subject, Map<String, Object> content, String templateName);

    /**
     * Send mail with attachments
     *
     * @param to             接受者
     * @param subject        主题
     * @param content        邮件内容
     * @param templateName   freemarker 模板名
     * @param attachFilePath 附件完整路径
     */
    void sendAttachMail(String to, String subject, Map<String, Object> content, String templateName, String attachFilePath);

    /**
     * 测试连接使用
     */
    void testConnection();
}
