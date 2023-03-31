package cn.aezo.chat_gpt.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.List;

// @Configuration // SqWxConfig中会使用@EnableConfigurationProperties进行激活
@Data
@ConfigurationProperties(prefix = "sq-mini-tools.miniapp")
public class SqWxProp {

    private List<Config> configs;

    @Data
    public static class Config {
        /**
         * 小程序内部代码
         */
        private String sqAppCode;

        /**
         * 设置微信小程序的appid
         */
        private String appid;

        /**
         * 设置微信小程序的Secret
         */
        private String secret;

        /**
         * 设置微信小程序消息服务器配置的token
         */
        private String token;

        /**
         * 设置微信小程序消息服务器配置的EncodingAESKey
         */
        private String aesKey;

        /**
         * 消息格式，XML或者JSON
         */
        private String msgDataFormat;
    }

}
