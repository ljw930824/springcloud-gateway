package com.ljw.gateway.business.blackwhitelist;

import java.util.Map;

/**
 * @ClassName: BlackWhiteList
 * @Description: IP过滤器
 * @Author: ljw
 * @Date: 2019/7/30 13:50
 **/
public interface BlackWhiteList {
    /**
     * 设置更新数据
     *
     * @param blackWhiteIPs
     */
    void setBlackWhiteIPs(Map<BlackWhiteListType, String> blackWhiteIPs);

    /**
     * 校验黑白IP
     *
     * @param blackWhiteIPListType
     * @param ip
     * @return
     */
    boolean check(BlackWhiteListType blackWhiteIPListType, String ip);
}
