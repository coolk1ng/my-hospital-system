package com.codesniper.service;

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
     * @param accessKeyId
     * @param accessKeySecret 
     * @return Boolean
     */
    Boolean sendMessage(String phone,String code, String accessKeyId, String accessKeySecret) throws Exception;
}
