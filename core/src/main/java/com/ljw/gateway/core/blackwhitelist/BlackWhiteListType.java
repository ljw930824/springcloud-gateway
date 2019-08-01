package com.ljw.gateway.core.blackwhitelist;

/**
 * @ClassName: BlackWhiteListType
 * @Description: IP 过滤类型
 * @Author: ljw
 * @Date: 2019/7/30 13:50
 **/
public enum BlackWhiteListType {

	/**
	 * 黑名单
	 */
	BLACKLIST,

	/**
	 * 白名单
	 */
	WHITELIST,
	
	/**
	 * 非黑白名单模式
	 */
	NON;

}
