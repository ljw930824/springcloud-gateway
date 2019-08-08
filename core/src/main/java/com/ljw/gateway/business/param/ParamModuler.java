package com.ljw.gateway.business.param;

import com.google.common.collect.Maps;

import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.net.URLDecoder;
import java.util.Map;

/**
 * @ClassName: ParamModuler
 * @Description: ParamModuler
 * @Author: ljw
 * @Date: 2019/7/30 13:50
 **/
public class ParamModuler {

    /**
     * 解析URL中的参数
     *
     * @param queryString
     * @param enc
     * @return
     */
    public static Map<String, String> getParamsMap(String queryString, String enc) {
        Map<String, String> params = Maps.newConcurrentMap();
        Map<String, String[]> paramsMap = getParamsArrayMap(queryString, enc);
        if (!paramsMap.isEmpty()) {
            for (Map.Entry<String, String[]> entry : paramsMap.entrySet()) {
                params.put(entry.getKey(), entry.getValue()[entry.getValue().length - 1]);
            }
        }
        return params;
    }

    /**
     * 解析URL中的参数
     *
     * @param queryString
     * @param enc
     * @return
     */
    public static Map<String, String[]> getParamsArrayMap(String queryString, String enc) {
        Map<String, String[]> paramsMap = Maps.newConcurrentMap();
        if (queryString != null && queryString.length() > 0) {
            int ampersandIndex, lastAmpersandIndex = 0;
            String subStr, param, value;
            String[] paramPair, values, newValues;

            do {
                ampersandIndex = queryString.indexOf('&', lastAmpersandIndex) + 1;
                if (ampersandIndex > 0) {
                    subStr = queryString.substring(lastAmpersandIndex, ampersandIndex - 1);
                    lastAmpersandIndex = ampersandIndex;
                } else {
                    subStr = queryString.substring(lastAmpersandIndex);
                }

                paramPair = subStr.split("=");
                param = paramPair[0];
                value = paramPair.length == 1 ? "" : paramPair[1];

                try {
                    value = URLDecoder.decode(value, enc);
                } catch (UnsupportedEncodingException ignored) {
					ignored.printStackTrace();
                }

                if (paramsMap.containsKey(param)) {
                    values = (String[]) paramsMap.get(param);
                    int len = values.length;
                    newValues = new String[len + 1];
                    System.arraycopy(values, 0, newValues, 0, len);
                    newValues[len] = value;
                } else {
                    newValues = new String[]{value};
                }
                paramsMap.put(param, newValues);
            } while (ampersandIndex > 0);
        }

        return paramsMap;
    }

    /**
     * 校验数据类型
     *
     * @param type
     * @param data
     * @return
     */
    public static boolean checkDataType(String type, String data) {
        switch (type) {
            case "int":
                try {
                    Integer.parseInt(data);
                    return true;
                } catch (NumberFormatException e) {
                    return false;
                }
            case "long":
                try {
                    Long.parseLong(data);
                    return true;
                } catch (NumberFormatException e) {
                    return false;
                }
            case "float":
                try {
                    Float.parseFloat(data);
                    return true;
                } catch (NumberFormatException e) {
                    return false;
                }
            case "short":
                try {
                    Short.parseShort(data);
                    return true;
                } catch (NumberFormatException e) {
                    return false;
                }
            case "double":
                try {
                    Double.parseDouble(data);
                    return true;
                } catch (NumberFormatException e) {
                    return false;
                }
            case "byte":
                try {
                    Byte.parseByte(data);
                    return true;
                } catch (NumberFormatException e) {
                    return false;
                }
            case "bigdecimal":
                try {
                    @SuppressWarnings("unused")
                    BigDecimal bg = new BigDecimal(data);
					return true;
                } catch (NumberFormatException e) {
                    return false;
                }
            case "boolean":
                try {
                    Boolean.parseBoolean(data);
                    return true;
                } catch (NumberFormatException e) {
                    return false;
                }
            default:
                throw new RuntimeException("非法类型");
        }
    }

}
