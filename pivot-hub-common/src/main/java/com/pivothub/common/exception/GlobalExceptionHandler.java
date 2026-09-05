package com.pivothub.common.exception;

import com.pivothub.common.result.Result;
import io.swagger.v3.oas.annotations.Hidden;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.List;

/**
 * @ClassName: GlobalExceptionHandler
 * @Description: 全局异常处理
 * @Author: lhb
 */

@RestControllerAdvice
@Hidden
public class GlobalExceptionHandler {

    public static final String CLIENT_ERROR_PREFIX = "CLIENT_ERROR:";
    public static final String VALIDATION_ERROR_PREFIX = "VALIDATION_ERROR:";

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<Result<Object>> handleRuntimeException(RuntimeException ex) {
        ex.printStackTrace();
        System.out.println("运行异常：" + ex.getMessage());
        Result<Object> result = Result.error(ex.getMessage());
        return new ResponseEntity<>(result, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    @ResponseBody
    public ResponseEntity<Result<Object>> handleIllegalArgumentException(IllegalArgumentException ex) {
        ex.printStackTrace();
        System.out.println("参数异常：" + ex.getMessage());
        String message = ex.getMessage();
        int statusCode;
        if (message.startsWith(CLIENT_ERROR_PREFIX)) {
            statusCode = HttpStatus.BAD_REQUEST.value();
            message = message.substring(CLIENT_ERROR_PREFIX.length()).trim();
        } else if (message.startsWith(VALIDATION_ERROR_PREFIX)) {
            statusCode = HttpStatus.UNPROCESSABLE_ENTITY.value();
            message = message.substring(VALIDATION_ERROR_PREFIX.length()).trim();
        } else {
            statusCode = HttpStatus.BAD_REQUEST.value();
        }
        Result<Object> result = new Result<>(statusCode, message);
        return new ResponseEntity<>(result, HttpStatus.valueOf(statusCode));
    }

    @ExceptionHandler(NullPointerException.class)
    @ResponseBody
    public ResponseEntity<Result<Object>> handleNullPointerException(NullPointerException ex) {
        ex.printStackTrace();
        System.out.println("空异常：" + ex.getMessage());
        Result<Object> result = Result.error(ex.getMessage());
        return new ResponseEntity<>(result, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseBody
    public ResponseEntity<Result<Object>> handleValidationExceptions(MethodArgumentNotValidException ex) {
        ex.printStackTrace();
        List<ObjectError> allErrors = ex.getBindingResult().getAllErrors();
        String errorMessage = allErrors.get(0).getDefaultMessage();
        Result<Object> result = Result.error(errorMessage);
        return new ResponseEntity<>(result, HttpStatus.BAD_REQUEST);
    }
}
