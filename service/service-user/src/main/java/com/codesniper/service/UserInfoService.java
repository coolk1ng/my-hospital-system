package com.codesniper.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.codesniper.yygh.model.user.UserInfo;
import com.codesniper.yygh.vo.user.LoginVo;

import java.util.Map;

/**
 * 用户信息Service
 *
 * @author CodeSniper
 * @since 2022/7/1 23:44
 */
public interface UserInfoService extends IService<UserInfo> {
    /** 
     * 登录
     * @param loginVo 
     * @return Map<String,Object>
     */
    Map<String,Object> login(LoginVo loginVo);

    /**
     * 根据openId查询用户
     * @param openId
     * @return UserInfo
     */
    UserInfo getUserInfoByOpenId(String openId);
}
