package com.pivothub.common.exception;

/** 业务服务业务异常。 */
public class BusinessException extends RuntimeException {
    private final int httpStatus;
    private final int resultCode;
    private final Object data;

    public BusinessException(String message) {
        this(400, message);
    }

    public BusinessException(int httpStatus, String message) {
        this(httpStatus, httpStatus, message, null);
    }

    public BusinessException(int httpStatus, String message, Object data) {
        this(httpStatus, httpStatus, message, data);
    }

    public BusinessException(int httpStatus, int resultCode, String message, Object data) {
        super(message);
        this.httpStatus = httpStatus;
        this.resultCode = resultCode;
        this.data = data;
    }

    public int getHttpStatus() {
        return httpStatus;
    }

    public int getResultCode() {
        return resultCode;
    }

    public Object getData() {
        return data;
    }
}
