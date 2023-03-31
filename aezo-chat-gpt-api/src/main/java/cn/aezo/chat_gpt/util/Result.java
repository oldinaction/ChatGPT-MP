package cn.aezo.chat_gpt.util;


import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

/**
 * @author smalle
 */
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Result<T> implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 成功
     */
    public static final String STATUS_SUCCESS = "success";
    /**
     * 失败(没有成功就是失败，报错错误)
     */
    public static final String STATUS_FAILURE = "failure";
    /**
     * 错误(一般错误信息不回透传到前台)
     */
    public static final String STATUS_ERROR = "error";
    public static final Integer CODE_SUCCESS = 20000;
    public static final Integer CODE_FAILURE = 40000;
    public static final Integer CODE_ERROR = 50000;
    public static final Integer CODE_UN_AUTH = 40100;

    /**
     * 成功失败状态
     */
    private String status;
    /**
     * 消息
     */
    private String message;
    /**
     * 返回数据
     */
    private T data;
    /**
     * 状态码
     */
    private Integer code;
    /**
     * 状态代码
     */
    private String codeKey;
    /**
     * 错误详细
     */
    private List errors;
    /**
     * 当前时间
     */
    private long timestamp = System.currentTimeMillis();

    /**
     * 非 success 则认为是请求失败(含请求出错)
     */
    public static boolean isSuccess(Result result) {
        return (result != null && Result.STATUS_SUCCESS.equals(result.getStatus()));
    }

    public static boolean isFailure(Result result) {
        return !isSuccess(result);
    }

    // 一般使用isFailure, 防止判断错误
    @Deprecated
    public static boolean isError(Result result) {
        return (result == null || Result.STATUS_ERROR.equals(result.getStatus()));
    }

    public static <T> Result<T> success() {
        Result result = new Result<>();
        result.status = Result.STATUS_SUCCESS;
        result.code = Result.CODE_SUCCESS;
        return result;
    }

    /**
     * 返回成功并附带成功消息. 为了和 success(T data) 区分, 因此取名 successMessage
     */
    public static <T> Result successMessage(String message) {
        Result<T> result = Result.success();
        result.message = message;
        return result;
    }

    /**
     * 返回成功并附带数据(Map/List/Bean...)
     */
    public static <T> Result<T> success(T data) {
        Result<T> result = Result.success();
        result.data = data;
        return result;
    }

    public static <T> Result<T> success(String message, T data) {
        Result<T> result = Result.success();
        result.message = message;
        result.data = data;
        return result;
    }

    public static <T> Result<T> success(String message, T data, String codeKey) {
        Result<T> result = Result.success();
        result.message = message;
        result.data = data;
        result.codeKey = codeKey;
        return result;
    }

    public static <T> Result<T> failure() {
        Result result = new Result<>();
        result.status = Result.STATUS_FAILURE;
        result.code = Result.CODE_FAILURE;
        return result;
    }

    public static <T> Result<T> failure(String message) {
        Result<T> result = Result.failure();
        result.message = message;
        return result;
    }

    public static <T> Result<T> failure(String message, String codeKey) {
        Result<T> result = Result.failure();
        result.message = message;
        result.codeKey = codeKey;
        return result;
    }

    public static <T> Result<T> failure(String message, Integer code) {
        Result<T> result = Result.failure();
        result.message = message;
        result.code = code;
        return result;
    }

    public static <T> Result<T> failure(String message, T data) {
        Result<T> result = Result.failure();
        result.message = message;
        result.data = data;
        return result;
    }

    public static <T> Result<T> failure(String message, String codeKey, T data) {
        Result<T> result = Result.failure();
        result.message = message;
        result.codeKey = codeKey;
        result.data = data;
        return result;
    }

    public static <T> Result<T> failure(String message, List errors, T data) {
        Result<T> result = Result.failure();
        result.message = message;
        result.errors = errors;
        result.data = data;
        return result;
    }

    public static <T> Result<T> failure(String message, Integer code, String codeKey, List errors, T data) {
        Result<T> result = Result.failure();
        result.message = message;
        result.code = code;
        result.codeKey = codeKey;
        result.errors = errors;
        result.data = data;
        return result;
    }

    public static <T> Result<T> error() {
        Result result = new Result<>();
        result.status = Result.STATUS_ERROR;
        result.code = Result.CODE_ERROR;
        return result;
    }

    public static <T> Result<T> error(String message) {
        Result<T> result = Result.error();
        result.message = message;
        return result;
    }

    public static <T> Result<T> error(String message, String codeKey) {
        Result<T> result = Result.error();
        result.message = message;
        result.codeKey = codeKey;
        return result;
    }

    public static <T> Result<T> error(String message, Integer code) {
        Result<T> result = Result.error();
        result.message = message;
        result.code = code;
        return result;
    }

    public static <T> Result<T> error(String message, T data) {
        Result<T> result = Result.error();
        result.message = message;
        result.data = data;
        return result;
    }

    public static <T> Result<T> error(String message, String codeKey, T data) {
        Result<T> result = Result.error();
        result.message = message;
        result.codeKey = codeKey;
        result.data = data;
        return result;
    }

    public static <T> Result<T> error(String message, List errors, T data) {
        Result<T> result = Result.error();
        result.message = message;
        result.errors = errors;
        result.data = data;
        return result;
    }

    public static <T> Result<T> error(String message, Integer code, String codeKey, List errors, T data) {
        Result<T> result = Result.error();
        result.message = message;
        result.code = code;
        result.codeKey = codeKey;
        result.errors = errors;
        result.data = data;
        return result;
    }
}
