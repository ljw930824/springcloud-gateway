package com.ljw.gateway.core.utils;

import java.util.Properties;

/**
 * @ClassName: PropertiesUtils
 * @Description: TODO
 * @Author: ljw
 * @Date: 2019/7/30 16:21
 **/
public class PropertiesUtils {

    public static final String COMMON = "application";
    public static final Properties properties = new Properties();

    public String getString(String key) {
        return properties.getProperty(key);
    }

    public Integer getInteger(String key) {
        try {
            return Integer.parseInt(properties.getProperty(key));
        } catch (Exception e) {
            return null;
        }
    }

    public Long getLong(String key) {
        try {
            return Long.parseLong(properties.getProperty(key));
        } catch (Exception e) {
            return null;
        }
    }

    public Boolean getBoolean(String key) {
        try {
            return Boolean.parseBoolean(properties.getProperty(key));
        } catch (Exception e) {
            return null;
        }
    }
}
