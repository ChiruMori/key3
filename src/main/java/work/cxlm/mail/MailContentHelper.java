package work.cxlm.mail;

import work.cxlm.model.entity.Notice;

import java.util.HashMap;
import java.util.Map;

/**
 * 辅助设置邮件内容的类
 * created 2020/12/12 20:50
 *
 * @author Chiru
 */
public class MailContentHelper {

    private final HashMap<String, Object> valueMap;

    public MailContentHelper() {
        valueMap = new HashMap<>();
    }

    public MailContentHelper setNotice(Notice notice) {
        valueMap.put("title", notice.getTitle());
        valueMap.put("content", notice.getContent());
        return this;
    }

    public MailContentHelper setMiniCodeUrl(String url) {
        valueMap.put("miniCodeUrl", url);
        return this;
    }

    public MailContentHelper setTitle(String title) {
        valueMap.put("title", title);
        return this;
    }

    public MailContentHelper setContent(String content) {
        valueMap.put("content", content);
        return this;
    }

    public MailContentHelper setTargetUserName(String name) {
        valueMap.put("targetUserName", name);
        return this;
    }

    public MailContentHelper setPublisherName(String name) {
        valueMap.put("publisherName", name);
        return this;
    }

    public Map<String, Object> build() {
        return valueMap;
    }
}
