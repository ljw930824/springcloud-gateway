package com.ljw.gateway.exception;

/**
 * @ClassName: LoadBalanceServiceException
 * @Description: 负载均衡服务异常
 * @Author: ljw
 * @Date: 2019/7/26 14:07
 **/
public class LoadBalanceServiceException extends ServiceException {

    public LoadBalanceServiceException(String msg) {
        super(msg);
    }

    public LoadBalanceServiceException(String msg, Throwable e) {
        super(msg, e);
    }

    public LoadBalanceServiceException(String msg, String code) {
        super(msg, code);
    }

    public LoadBalanceServiceException(String msg, String code, Throwable e) {
        super(msg, code, e);
    }
}
