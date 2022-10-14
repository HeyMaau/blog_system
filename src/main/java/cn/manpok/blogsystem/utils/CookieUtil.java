package cn.manpok.blogsystem.utils;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 设置cookie，获取cookie工具类
 */
public class CookieUtil {

    /**
     * cookie的域名
     */
    private static final String DOMAIN = "localhost";

    public static void setupCookie(HttpServletResponse response, String key, String value) {
        setupCookie(response, key, value, Constants.TimeValue.YEAR);
    }

    /**
     * @param response
     * @param key
     * @param value
     * @param ttl      过期时间 单位：秒
     */
    public static void setupCookie(HttpServletResponse response, String key, String value, int ttl) {
        Cookie cookie = new Cookie(key, value);
        cookie.setDomain(DOMAIN);
        cookie.setMaxAge(ttl);
        cookie.setPath("/");
        response.addCookie(cookie);
    }

    public static String getCookie(HttpServletRequest request, String key) {
        Cookie[] cookies = request.getCookies();
        if (cookies == null) {
            return null;
        }
        for (Cookie cookie : cookies) {
            if (cookie.getName().equals(key)) {
                return cookie.getValue();
            }
        }
        return null;
    }

    public static void deleteCookie(HttpServletResponse response, String key) {
        setupCookie(response, key, null, 0);
    }
}
