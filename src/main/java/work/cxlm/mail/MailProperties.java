package work.cxlm.mail;

import org.apache.commons.lang3.StringUtils;

import java.util.HashMap;
import java.util.Map;

/**
 * created 2020/11/9 14:56
 *
 * @author Chiru
 * @author johnniang
 */
public class MailProperties extends org.springframework.boot.autoconfigure.mail.MailProperties {

    private Map<String, String> properties;

    public MailProperties() {
        this(false);
    }

    /**
     * 方便测试使用
     */
    public MailProperties(boolean needDebug) {
        addProperties("mail.debug", Boolean.toString(needDebug));
        addProperties("mail.smtp.auth", Boolean.TRUE.toString());
        addProperties("mail.smtp.ssl.enable", Boolean.TRUE.toString());
        addProperties("mail.smtp.timeout", "10000");
    }

    private void addProperties(String k, String v) {
        if (properties == null) {
            properties = new HashMap<>(7);
        }
        properties.put(k, v);
    }

    @Override
    public Map<String, String> getProperties() {
        return properties;
    }

    public void setProperties(Map<String, String> properties) {
        this.properties = properties;
    }

    @Override
    public String toString() {
        final String br = ",\n";
        return "MailProperties{" + br +
                "host: " + getHost() + br +
                "username: " + getUsername() + br +
                "password: " + (StringUtils.isBlank(getPassword()) ? "<null>" : "<non-null>") + br +
                "port: " + getPort() + br +
                "protocol: " + getProtocol() + br +
                "defaultEncoding: " + getDefaultEncoding() + br +
                "properties: " + properties + br +
                "}";
    }
}
