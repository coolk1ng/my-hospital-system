package com.codesniper.service;


import com.baomidou.mybatisplus.extension.service.IService;
import com.codesniper.yygh.model.hosp.HospitalSet;
import com.codesniper.yygh.vo.order.SignInfoVo;

/**
 * 医院设置
 *
 * @author CodeSniper
 * @since 2022-05-29
 */
public interface HospitalSetService extends IService<HospitalSet> {
    
    /** 
     * 根据hoscode查询signKey
     * @param hoscode 
     * @return String
     */
    String getSignKey(String hoscode);

    /**
     * 获取签名信息
     * @param hoscode
     * @return SignInfoVo
     */
    SignInfoVo getSignInfo(String hoscode);
}
