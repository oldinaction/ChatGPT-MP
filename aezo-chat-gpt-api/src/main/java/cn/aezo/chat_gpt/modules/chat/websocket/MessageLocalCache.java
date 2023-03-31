package cn.aezo.chat_gpt.modules.chat.websocket;

import cn.hutool.cache.CacheUtil;
import cn.hutool.cache.impl.TimedCache;
import cn.hutool.core.date.DateUnit;

/**
 * 用户发送消息的本地缓存
 */
public class MessageLocalCache {
    /**
     * 缓存时长
     */
    public static final long TIMEOUT = 5 * DateUnit.MINUTE.getMillis();
    /**
     * 清理间隔
     */
    private static final long CLEAN_TIMEOUT = 5 * DateUnit.MINUTE.getMillis();
    /**
     * 缓存对象
     */
    public static final TimedCache<String, Object> CACHE = CacheUtil.newTimedCache(TIMEOUT);

    static {
        //启动定时任务
        CACHE.schedulePrune(CLEAN_TIMEOUT);
    }
}
