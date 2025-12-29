package com.chanshop.common.exception;

public class DuplicateException extends BusinessException {
    public DuplicateException(ErrorCode errorCode) {
        super(errorCode);
    }

    public DuplicateException(String message, ErrorCode errorCode) {
        super(message, errorCode);
    }
}
