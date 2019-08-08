package com.ljw.gateway.utils;

import java.util.Properties;

/**
 * @ClassName: PropertiesUtils
 * @Description: PropertiesUtils
 * @Author: ljw
 * @Date: 2019/7/30 16:21
 **/
public class PropertiesUtils {

    public static final String COMMON = "application";
    public static final Properties PROPERTIES = new Properties();

    public String getString(String key) {
        return PROPERTIES.getProperty(key);
    }

    public Integer getInteger(String key) {
        try {
            return Integer.parseInt(PROPERTIES.getProperty(key));
        } catch (Exception e) {
            return null;
        }
    }

    public Long getLong(String key) {
        try {
            return Long.parseLong(PROPERTIES.getProperty(key));
        } catch (Exception e) {
            return null;
        }
    }

    public Boolean getBoolean(String key) {
        try {
            return Boolean.parseBoolean(PROPERTIES.getProperty(key));
        } catch (Exception e) {
            return null;
        }
    }
}
