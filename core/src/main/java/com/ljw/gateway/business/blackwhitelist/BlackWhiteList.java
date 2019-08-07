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
     * @param blackWhiteListTypeStringMap
     */
    void setBlackWhiteIPs(Map<BlackWhiteListType, String> blackWhiteListTypeStringMap);

    /**
     * 校验黑白IP
     *
     * @param blackWhiteListType
     * @param ip
     * @return
     */
    boolean check(BlackWhiteListType blackWhiteListType, String ip);
}
