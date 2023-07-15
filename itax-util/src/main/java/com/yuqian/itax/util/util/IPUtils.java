package com.yuqian.itax.util.util;

import javax.servlet.http.HttpServletRequest;

public class IPUtils {
    public static final String UNKNOWN = "unknown";

    private IPUtils(){}

    public static long ipToLong(String strIp) {
        try {
            long[] ip = new long[4];
            int position1 = strIp.indexOf('.');
            int position2 = strIp.indexOf('.', position1 + 1);
            int position3 = strIp.indexOf('.', position2 + 1);
            ip[0] = Long.parseLong(strIp.substring(0, position1));
            ip[1] = Long.parseLong(strIp.substring(position1 + 1, position2));
            ip[2] = Long.parseLong(strIp.substring(position2 + 1, position3));
            ip[3] = Long.parseLong(strIp.substring(position3 + 1));
            return (ip[0] << 24) + (ip[1] << 16) + (ip[2] << 8) + ip[3];
        } catch (Exception e) {
            return 0;
        }
    }

    public static String longToIP(long longIp) {
        try {
            StringBuilder sb = new StringBuilder("");
            sb.append(String.valueOf((longIp >>> 24)));
            sb.append(".");
            sb.append(String.valueOf((longIp & 0x00FFFFFF) >>> 16));
            sb.append(".");
            sb.append(String.valueOf((longIp & 0x0000FFFF) >>> 8));
            sb.append(".");
            sb.append(String.valueOf((longIp & 0x000000FF)));
            return sb.toString();
        } catch (Exception e) {
            return "";
        }
    }

    /**
     * 获取请求ip
     * @param request
     * @return
     */
    public static String getRequestIp(HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");
        if (ip == null || ip.length() == 0 || UNKNOWN.equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        } else {
            //多次反向代理后会有多个ip值，第一个ip才是真实ip
            int index = ip.indexOf(',');
            if (index != -1) {
                return ip.substring(0, index);
            } else {
                return ip;
            }
        }
        if (ip == null || ip.length() == 0 || UNKNOWN.equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ip == null || ip.length() == 0 || UNKNOWN.equalsIgnoreCase(ip)) {
            ip = request.getHeader("HTTP_CLIENT_IP");
        }
        if (ip == null || ip.length() == 0 || UNKNOWN.equalsIgnoreCase(ip)) {
            ip = request.getHeader("HTTP_X_FORWARDED_FOR");
        }
        if (ip == null || ip.length() == 0 || UNKNOWN.equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        return ip;
    }

}
