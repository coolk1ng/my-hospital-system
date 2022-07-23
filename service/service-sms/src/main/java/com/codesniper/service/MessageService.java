package com.codesniper.service;

import com.codesniper.yygh.vo.msm.MsmVo;

/**
 * 短信Service
 *
 * @author CodeSniper
 * @since 2022/7/3 01:30
 */
public interface MessageService {
    
    /** 
     * 发送短信
     * @param phone
     * @param code
     * @return Boolean
     */
    Boolean sendMessage(String phone,String code) throws Exception;

    /**
     * mq发送短信
     * @param msmVo
     * @return Boolean
     */
    Boolean sendMessage(MsmVo msmVo) throws Exception;
}
