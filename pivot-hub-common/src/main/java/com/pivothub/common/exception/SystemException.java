package com.pivothub.common.exception;

/** 系统服务业务异常。 */
public class SystemException extends RuntimeException {
    private final int httpStatus;

    public SystemException(String message) {
        this(400, message);
    }

    public SystemException(int httpStatus, String message) {
        super(message);
        this.httpStatus = httpStatus;
    }

    public int getHttpStatus() {
        return httpStatus;
    }
}
