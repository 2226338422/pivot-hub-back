package com.pivothub.common.exception;

/** 业务服务业务异常。 */
public class BusinessException extends RuntimeException {
    private final int httpStatus;

    public BusinessException(String message) {
        this(400, message);
    }

    public BusinessException(int httpStatus, String message) {
        super(message);
        this.httpStatus = httpStatus;
    }

    public int getHttpStatus() {
        return httpStatus;
    }
}
