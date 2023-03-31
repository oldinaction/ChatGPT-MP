package cn.aezo.chat_gpt.limit;

import cn.aezo.chat_gpt.config.BizException;
import cn.hutool.cache.Cache;
import cn.hutool.cache.CacheUtil;
import cn.hutool.extra.servlet.ServletUtil;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import java.util.concurrent.atomic.AtomicInteger;

@Aspect
@Component
@Slf4j
public class LimitRequestNumAspect {
    Cache<String, AtomicInteger> cacheOne = CacheUtil.newLRUCache(1000);
    Cache<String, AtomicInteger> cacheTwo = CacheUtil.newLRUCache(1000);

    /**
     * 切点
     */
    @Pointcut("@annotation(limitRequestNum)")
    public void pointCut(LimitRequestNum limitRequestNum){}

    @Around(value = "pointCut(limitRequestNum)", argNames = "joinPoint,limitRequestNum")
    public Object requestLimit(ProceedingJoinPoint joinPoint, LimitRequestNum limitRequestNum) throws Throwable {
        Object[] args = joinPoint.getArgs();
        check((HttpServletRequest) args[0], cacheOne, 60);
        check((HttpServletRequest) args[0], cacheTwo, 120);
        return joinPoint.proceed();
    }

    private void check(HttpServletRequest request, Cache<String, AtomicInteger> cache, int max) {
        String ip = ServletUtil.getClientIP(request);
        if (cache.containsKey(ip)) {
            AtomicInteger count = cache.get(ip);
            if (count.get() >= max) {
                // 返回错误信息
                log.error("频繁请求IP: " + ip);
                throw new BizException("您的请求过于频繁，请稍后再试");
            } else {
                count.incrementAndGet();
            }
        } else {
            AtomicInteger count = new AtomicInteger(1);
            cache.put(ip, count);
        }
        // 执行业务逻辑
    }

    /**
     * 定时将计数器重置
     */
    @Scheduled(cron = "0 0 0/1 * * ?")
    //@Scheduled(cron = "0 0/5 * * * ?")
    private void countScheduledTasks() {
        cacheOne.clear();
    }

    /**
     * 定时将计数器重置
     */
    @Scheduled(cron = "0 0 23 * * ?")
    private void countScheduledTasks2() {
        cacheTwo.clear();
    }
}
