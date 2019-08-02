package com.ljw.gateway.core.blackwhitelist;

import com.google.common.collect.Maps;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Vector;
import java.util.regex.Pattern;

/**
 * 该过滤器用于过滤非指定列表中的IP不能访问指定功能权限<br>
 * <br>
 * 支持IP段*通配符配置，如:<br>
 * 1.所有IP: *<br>
 * 2.所有192.168.开头的IP: 192.168.*<br>
 * 3.所有0网段的IP: 192.168.0.*<br>
 * 4.也支持这种格式的IP段指定: 10.195.13.0-10.195.13.255<br>
 * 5.单独IP指定: 192.168.1.100<br>
 * 6.所有以192.168.1开头.50结尾的IP，即192.168.10.50-192.168.19.50;
 * 192.168.100.50-192.168.199.50: 192.168.1*.50<br>
 */
@Component
@Slf4j
public class BlackWhiteListFactory implements BlackWhiteList {

    /**
     * 允许的IP访问列表
     */
    private Map<BlackWhiteListType, Vector<String>> ipFilterMap = Maps.newConcurrentMap();

    /**
     * IP的正则
     */
    public static Pattern pattern = Pattern
            .compile("(1\\d{1,2}|2[0-4]\\d|25[0-5]|\\d{1,2})\\."
                    + "(1\\d{1,2}|2[0-4]\\d|25[0-5]|\\d{1,2})\\."
                    + "(1\\d{1,2}|2[0-4]\\d|25[0-5]|\\d{1,2})\\."
                    + "(1\\d{1,2}|2[0-4]\\d|25[0-5]|\\d{1,2})");

    @Override
    public synchronized void setBlackWhiteIPs(Map<BlackWhiteListType, String> blackWhiteIPs) {
        ipFilterMap.clear();
        for (Map.Entry<BlackWhiteListType, String> bwIP : blackWhiteIPs
                .entrySet()) {
            // 192.168.0.*转换为192.168.0.1-192.168.0.255
            for (String allow : bwIP.getValue().replaceAll("\\s", "")
                    .split(";")) {
                if (allow.indexOf("*") > -1) {
                    String[] ips = allow.split("\\.");
                    String[] from = new String[]{"0", "0", "0", "0"};
                    String[] end = new String[]{"255", "255", "255", "255"};
                    List<String> tem = new ArrayList<String>();
                    for (int i = 0; i < ips.length; i++) {
                        if (ips[i].indexOf("*") > -1) {
                            tem = complete(ips[i]);
                            from[i] = null;
                            end[i] = null;
                        } else {
                            from[i] = ips[i];
                            end[i] = ips[i];
                        }
                    }

                    StringBuffer fromIP = new StringBuffer();
                    StringBuffer endIP = new StringBuffer();
                    for (int i = 0; i < 4; i++) {
                        if (from[i] != null) {
                            fromIP.append(from[i]).append(".");
                            endIP.append(end[i]).append(".");
                        } else {
                            fromIP.append("[*].");
                            endIP.append("[*].");
                        }
                    }
                    fromIP.deleteCharAt(fromIP.length() - 1);
                    endIP.deleteCharAt(endIP.length() - 1);

                    for (String s : tem) {
                        String ip = fromIP.toString().replace("[*]",
                                s.split(";")[0])
                                + "-"
                                + endIP.toString().replace("[*]",
                                s.split(";")[1]);
                        if (validate(ip)) {
                            Vector<String> ipList = ipFilterMap.get(bwIP.getKey());
                            if (ipList == null || ipList.isEmpty()) {
                                ipList = new Vector<>();
                            }
                            ipList.add(ip);
                            ipFilterMap.put(bwIP.getKey(), ipList);
                        }
                    }
                } else {
                    if (validate(allow)) {
                        Vector<String> ipList = ipFilterMap.get(bwIP.getKey());
                        if (ipList == null || ipList.isEmpty()) {
                            ipList = new Vector<>();
                        }
                        ipList.add(allow);
                        ipFilterMap.put(bwIP.getKey(), ipList);
                    }
                }
            }
        }
    }

    @Override
    public boolean check(BlackWhiteListType blackWhiteIPListType, String ip) {
        Vector<String> ipList = ipFilterMap.get(blackWhiteIPListType);
        if (ipList == null || ipList.isEmpty() || ipList.contains(ip)) {
            if (BlackWhiteListType.BLACKLIST == blackWhiteIPListType) {
                return false;
            } else if (BlackWhiteListType.WHITELIST == blackWhiteIPListType) {
                return true;
            } else {
                throw new RuntimeException("非法类型");
            }
        } else {
            for (String allow : ipList) {
                if (allow.indexOf("-") > -1) {
                    String allowTemp = allow;
                    String[] from = allowTemp.split("-")[0].split("\\.");
                    String[] end = allowTemp.split("-")[1].split("\\.");
                    String[] tag = ip.split("\\.");

                    // 对IP从左到右进行逐段匹配
                    boolean check = true;
                    for (int i = 0; i < 4; i++) {
                        int s = Integer.valueOf(from[i]);
                        int t = Integer.valueOf(tag[i]);
                        int e = Integer.valueOf(end[i]);
                        if (!(s <= t && t <= e)) {
                            check = false;
                            break;
                        }
                    }
                    if (check) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    /**
     * 对单个IP节点进行范围限定
     *
     * @param arg
     * @return 返回限定后的IP范围，格式为List[10;19, 100;199]
     */
    private List<String> complete(String arg) {
        List<String> com = new ArrayList<String>();
        if (arg.length() == 1) {
            com.add("0;255");
        } else if (arg.length() == 2) {
            String s1 = complete(arg, 1);
            if (s1 != null) {
                com.add(s1);
            }
            String s2 = complete(arg, 2);
            if (s2 != null) {
                com.add(s2);
            }
        } else {
            String s1 = complete(arg, 1);
            if (s1 != null) {
                com.add(s1);
            }
        }

        return com;
    }

    private String complete(String arg, int length) {
        String from = "";
        String end = "";
        if (length == 1) {
            from = arg.replace("*", "0");
            end = arg.replace("*", "9");
        } else {
            from = arg.replace("*", "00");
            end = arg.replace("*", "99");
        }
        if (Integer.valueOf(from) > 255) {
            return null;
        }
        if (Integer.valueOf(end) > 255) {
            end = "255";
        }

        return from + ";" + end;
    }

    /**
     * 在添加至白名单时进行格式校验
     *
     * @param ip
     * @return
     */
    private boolean validate(String ip) {
        for (String s : ip.split("-")) {
            if (!pattern.matcher(s).matches()) {
                return false;
            }
        }
        return true;
    }

}