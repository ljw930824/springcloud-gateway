package com.ljw.gateway.exception;

/**
 * url重复异常
 */
public class UrlDeplicateServiceException extends ServiceException {

    public UrlDeplicateServiceException(String msg) {
        super(msg);
    }

    public UrlDeplicateServiceException(String msg, Throwable e) {
        super(msg, e);
    }

    public UrlDeplicateServiceException(String msg, String code) {
        super(msg, code);
    }

    public UrlDeplicateServiceException(String msg, String code, Throwable e) {
        super(msg, code, e);
    }
}
