package com.codesniper.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.codesniper.yygh.model.user.UserInfo;
import com.codesniper.yygh.vo.user.LoginVo;
import com.codesniper.yygh.vo.user.UserAuthVo;
import com.codesniper.yygh.vo.user.UserInfoQueryVo;

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

    /**
     * 用户认证
     * @param currentUserId
     * @param userAuthVo
     * @return Object
     */
    Boolean userAuth(Long currentUserId, UserAuthVo userAuthVo);

    /**
     * 查询用户列表
     * @param userInfoQueryVo
     * @return IPage<UserInfo>
     */
    IPage<UserInfo> getUserList(UserInfoQueryVo userInfoQueryVo);

    /**
     * 锁定,解锁用户
     * @param userInfoQueryVo
     * @return Boolean
     */
    Boolean lockAndUnLock(UserInfoQueryVo userInfoQueryVo);

    /**
     * 查询用户详情
     * @param userId
     * @return Map<String,Object>
     */
    Map<String,Object> getUserDetail(Long userId);

    /**
     * 用户认证审批
     * @param userInfoQueryVo
     * @return boolean
     */
    boolean approval(UserInfoQueryVo userInfoQueryVo);
}
