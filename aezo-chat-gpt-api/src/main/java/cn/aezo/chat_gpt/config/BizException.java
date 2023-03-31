package cn.aezo.chat_gpt.config;

import lombok.Data;

import java.util.Map;

/**
 * @author smalle
 * @since 2020-12-16 14:40
 */
@Data
public class BizException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    private String message;
    private String errorCodeKey;
    private Map<String, Object> data;

    public BizException(String message) {
        super(message);
        this.message = message;
    }

    public BizException(String message, String errorCodeKey) {
        super(message);
        this.message = message;
        this.errorCodeKey = errorCodeKey;
    }

    public BizException(String message, Exception e) {
        super(message, e);
        this.message = message;
    }

    public BizException(String message, String errorCodeKey, Exception e) {
        super(message, e);
        this.message = message;
        this.errorCodeKey = errorCodeKey;
        this.data = data;
    }

    public BizException(String message, String errorCodeKey, Map<String, Object> data, Exception e) {
        super(message, e);
        this.message = message;
        this.errorCodeKey = errorCodeKey;
    }
}
