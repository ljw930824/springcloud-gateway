package com.ljw.gateway.exception;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @ClassName: ServiceException
 * @Description: 服务异常
 * @Author: ljw
 * @Date: 2019/7/26 14:07
 **/
@Data
@EqualsAndHashCode(callSuper = false)
public class ServiceException extends RuntimeException {
    private static final long serialVersionUID = 2956071387027987098L;

    private String msg;
    // 通用错误为-1，其他错误应该有编码
    private String code = "-1";

    public ServiceException(String msg) {
        super(msg);
        this.msg = msg;
    }

    public ServiceException(String msg, Throwable e) {
        super(msg, e);
        this.msg = msg;
    }

    public ServiceException(String msg, String code) {
        super(msg);
        this.msg = msg;
        this.code = code;
    }

    public ServiceException(String msg, String code, Throwable e) {
        super(msg, e);
        this.msg = msg;
        this.code = code;
    }

}
