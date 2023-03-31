package cn.aezo.chat_gpt.config;

import cn.aezo.chat_gpt.util.Result;
import cn.dev33.satoken.exception.NotLoginException;
import cn.dev33.satoken.exception.NotPermissionException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * 异常处理器
 */
@Slf4j
@RestControllerAdvice
public class AppExceptionHandler {

    @ExceptionHandler(BizException.class)
    public Result handleBizException(BizException e) {
        log.error("", e);
        return Result.failure(e.getMessage());
    }

    @ExceptionHandler(NotLoginException.class)
    public Result handleNotLoginException(NotLoginException e) {
        log.error("", e);
        return Result.failure("令牌失效或尚未登录", "auth.login.fail");
    }

    @ExceptionHandler(NotPermissionException.class)
    public Result handleNotPermissionException(NotPermissionException e) {
        log.error("", e);
        return Result.failure("无此资源权限", "auth.perm.fail");
    }

    @ExceptionHandler(Exception.class)
    public Result handleException(Exception e) {
        log.error("", e);
        return Result.failure("服务器出小差了，请稍后再试吧~");
    }
}
