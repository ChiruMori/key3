package work.cxlm.utils.spring;

import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.stereotype.Component;

/**
 * 操作 SpringApplicationContext 相关的工具类，使用本类时，需要在主类扫描本类所在包
 * create 2021/4/11 18:15
 *
 * @author Chiru
 */
@Component
@Slf4j
public class SpringContextUtils implements ApplicationContextAware {

    private static ApplicationContext applicationContext = null;

    @Override
    public synchronized void setApplicationContext(@NonNull ApplicationContext applicationContext) throws BeansException {
        if (SpringContextUtils.applicationContext == null) {
            SpringContextUtils.applicationContext = applicationContext;
        }
    }

    public static synchronized ApplicationContext getApplicationContext() {
        if (applicationContext == null) {
            log.warn("SpringContextUtil 读取了配置文件以生成上下文");
            applicationContext = new ClassPathXmlApplicationContext(new String[]{"applicationContext-*.xml"});
        }
        return applicationContext;
    }

    public static Object getBean(String beanName) throws BeansException {
        return getApplicationContext().getBean(beanName);
    }

    public static <T> T getBean(String beanName, Class<T> requiredType) throws BeansException {
        return getApplicationContext().getBean(beanName, requiredType);
    }

    public static <T> T getBean(Class<T> requiredType) throws BeansException {
        return getApplicationContext().getBean(requiredType);
    }

    public static boolean containsBean(String beanName) {
        return getApplicationContext().containsBean(beanName);
    }

    public static boolean isSingleton(String beanName) throws NoSuchBeanDefinitionException {
        return getApplicationContext().isSingleton(beanName);
    }

    public static Class<?> getType(String beanName) throws NoSuchBeanDefinitionException {
        return getApplicationContext().getType(beanName);
    }

    public static String[] getAliases(String beanName) throws NoSuchBeanDefinitionException {
        return getApplicationContext().getAliases(beanName);
    }

}