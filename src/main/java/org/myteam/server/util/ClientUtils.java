package org.myteam.server.util;

import jakarta.servlet.http.HttpServletRequest;

/**
 * IP를 가져오기 위한 Utils
 */
public class ClientUtils {

    public static String getRemoteIP(HttpServletRequest request) {
        String ip = request.getHeader("X-FORWARDED-FOR");

        if (ip != null && !ip.isEmpty() && !"unknown".equalsIgnoreCase(ip)) {
            // X-FORWARDED-FOR 헤더에 여러 IP가 있을 경우, 첫 번째 IP만 추출
            String[] ips = ip.split(",");
            ip = ips[0].trim();
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("HTTP_CLIENT_IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("HTTP_X_FORWARDED_FOR");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }

        return ip;
    }

    /**
     * IP 뒷자리 마스크 처리
     */
    public static String maskIp(String ip) {
        if (ip == null || !ip.matches("\\d+\\.\\d+\\.\\d+\\.\\d+")) {
            return ip;
        }
        return ip.replaceAll("(\\d+\\.\\d+\\.)(\\d+)(\\.)(\\d+)", "$1***$3**");
    }

    /**
     * int로 변환
     */
    public static int toInt(Object val) {
        return val != null ? Integer.parseInt(val.toString()) : 0;
    }
}