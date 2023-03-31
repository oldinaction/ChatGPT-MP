package cn.aezo.chat_gpt;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/**
 * @author smalle
 */
@EnableTransactionManagement
@EnableScheduling
@EnableAspectJAutoProxy(exposeProxy = true)
@MapperScan({"cn.aezo.chat_gpt.**.mapper"})
@ComponentScan({"cn.aezo.chat_gpt"})
@SpringBootApplication
public class ChatGptApplication {

    public static void main(String[] args) {
        SpringApplication.run(ChatGptApplication.class, args);
    }

}
