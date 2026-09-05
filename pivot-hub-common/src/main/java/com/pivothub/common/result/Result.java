package com.pivothub.common.result;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.servlet.http.HttpServletResponse;
import lombok.Data;

/**
 * @ClassName: Result
 * @Description: 统一返回结果
 * @Author: lhb
 */

@Data
public class Result<T> {

    @Schema(title = "返回数据")
    private T data;
    @Schema(title = "状态码")
    private Integer code;
    @Schema(title = "信息")
    private String msg;

    public Result() {
    }

    public Result(Integer code) {
        this.code = code;
    }

    public Result(Integer code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public Result(T data, Integer code) {
        this.code = code;
        this.data = data;
    }

    public Result(T data, Integer code, String msg) {
        this.data = data;
        this.code = code;
        this.msg = msg;
    }

    public static Result<Object> success() {
        return new Result<>(HttpServletResponse.SC_CREATED);
    }

    public static <T> Result<T> success(T data) {
        return new Result<>(data, HttpServletResponse.SC_CREATED);
    }

    public static <T> Result<T> success(T data, String msg) {
        return new Result<>(data, HttpServletResponse.SC_CREATED, msg);
    }

    public static Result<Object> error() {
        return new Result<>(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
    }

    public static <T> Result<T> error(T data, String msg) {
        return new Result<>(data, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, msg);
    }

    public static Result<Object> error(Integer code) {
        return new Result<>(code);
    }

    public static Result<Object> error(String message) {
        return new Result<>(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, message);
    }

    public static Result<Object> error(Integer code, String message) {
        return new Result<>(code, message);
    }
}
