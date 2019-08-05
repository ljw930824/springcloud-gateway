package com.ljw.gateway.common.model;

import lombok.Data;

/**
 * @ClassName: ReturnData
 * @Description: 返回实体
 * @Author: ljw
 * @Date: 2019/7/26 16:19
 **/
@Data
public class ReturnData<T> {

    private int code = 200;
    private String message = "success";

    private T data;
}
