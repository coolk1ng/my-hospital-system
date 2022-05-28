package com.codesniper.service.impl;


import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.codesniper.mapper.HospitalSetMapper;
import com.codesniper.service.HospitalSetService;
import com.codesniper.yygh.model.hosp.HospitalSet;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 医院设置
 *
 * @author CodeSniper
 * @since 2022-05-29
 */
@Service("HospitalSetService")
public class HospitalSetServiceImpl extends ServiceImpl<HospitalSetMapper, HospitalSet> implements HospitalSetService {

    @Autowired
    private HospitalSetMapper hospitalSetMapper;
}
