package com.codesniper.common.utils;

import com.codesniper.common.helper.JwtHelper;

import javax.servlet.http.HttpServletRequest;

/**
 * 用户信息工具类
 *
 * @author CodeSniper
 * @since 2022/7/7 14:20
 */
public class AuthContextHolder {
    
    /** 
     * 获取当前用户id
     * @param request 
     * @return Long
     */
    public static Long getCurrentUserId(HttpServletRequest request) {
        // 从header中获取token
        String token = request.getHeader("token");
        return JwtHelper.getUserId(token);
    }

    /**
     * 获取当前用户名称
     * @param request
     * @return String
     */
    public static String getCurrentUserName(HttpServletRequest request) {
        // 从header中获取token
        String token = request.getHeader("token");
        return JwtHelper.getUserName(token);
    }
}
