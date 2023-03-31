package cn.aezo.chat_gpt.config;

import cn.dev33.satoken.interceptor.SaInterceptor;
import cn.dev33.satoken.router.SaRouter;
import cn.dev33.satoken.stp.StpUtil;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.converter.Converter;
import org.springframework.format.FormatterRegistry;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

@Configuration
public class CustomWebMvcConfigurer implements WebMvcConfigurer {
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new SaInterceptor(handle -> {
                    SaRouter
                            .match("/**")
                            .notMatch("/tools/**")
                            .notMatch("/core/user/login")
                            .check(r -> StpUtil.checkLogin());
                    SaRouter.match("/admin/**", r -> StpUtil.checkPermission("admin"));
                }))
                .addPathPatterns("/**");
    }

    @Override
    public void addFormatters(FormatterRegistry registry) {
        registry.addConverter(new StringToDateConverter());
    }

    public class StringToDateConverter implements Converter<String, Date> {
        public static final String dateFormatShort = "yyyy/MM/dd";
        public static final String dateFormatShort2 = "yyyy-MM-dd";
        public static final String dateFormat = "yyyy/MM/dd HH:mm:ss";
        public static final String dateFormat2 = "yyyy-MM-dd HH:mm:ss";

        @Override
        public Date convert(String value) {
            return parseDate(value);
        }


        public Date parseDate(Object dateString) {
            if(StringUtils.isEmpty(dateString)) {
                return null;
            }

            try {
                String value = (String) dateString;
                value = value.trim();

                if(value.contains("-") || value.contains("/")) {
                    SimpleDateFormat formatter;
                    Date dtDate = null;
                    if(value.contains(":")) {
                        if(value.contains("T") && value.contains("Z")) {
                            // 前端js插件qs默认格式化时间为1970-01-01T00:00:00.007Z
                            formatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
                            formatter.setTimeZone(TimeZone.getTimeZone("UTC"));
                        } else {
                            try {
                                formatter = new SimpleDateFormat(dateFormat);
                                dtDate = formatter.parse(value);
                            } catch (Exception e) {
                                formatter = new SimpleDateFormat(dateFormat2);
                                dtDate = formatter.parse(value);
                            }
                        }
                    } else {
                        try {
                            formatter = new SimpleDateFormat(dateFormatShort);
                            dtDate = formatter.parse(value);
                        } catch (Exception e) {
                            formatter = new SimpleDateFormat(dateFormatShort2);
                            dtDate = formatter.parse(value);
                        }
                    }

                    return dtDate;
                } else if(value.matches("^\\d+$")) {
                    Long lDate = new Long(value);
                    return new Date(lDate);
                }
            } catch (Exception e) {
                throw new RuntimeException(String.format("parser %s to Date fail", dateString));
            }
            throw new RuntimeException(String.format("parser %s to Date fail", dateString));
        }
    }
}
