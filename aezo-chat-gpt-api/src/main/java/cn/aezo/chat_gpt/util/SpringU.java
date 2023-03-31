package cn.aezo.chat_gpt.util;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

/**
 * Spring常用工具
 * @author smalle
 * @date 2020-11-22 16:42
 */
@Component
public class SpringU implements ApplicationContextAware {

    private static ApplicationContext applicationContext;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        if(SpringU.applicationContext == null) {
            SpringU.applicationContext = applicationContext;
        }
    }

    /**
     * 获取applicationContext
     * @author smalle
     * @since 2020/11/29
     * @return org.springframework.context.ApplicationContext
     */
    public static ApplicationContext getApplicationContext() {
        return applicationContext;
    }

    /**
     * 通过name获取 Bean
     * @author smalle
     * @since 2020/11/29
     * @param name
     * @throws
     * @return java.lang.Object
     */
    public static Object getBean(String name){
        return getApplicationContext().getBean(name);
    }

    /**
     * 通过class获取Bean
     * @author smalle
     * @since 2020/11/29
     * @param clazz
     * @return T
     */
    public static <T> T getBean(Class<T> clazz){
        return getApplicationContext().getBean(clazz);
    }

    /**
     * 通过name以及Clazz返回指定的Bean
     * @author smalle
     * @since 2020/11/29
     * @param name
     * @param clazz
     * @return T
     */
    public static <T> T getBean(String name,Class<T> clazz){
        return getApplicationContext().getBean(name, clazz);
    }
}
