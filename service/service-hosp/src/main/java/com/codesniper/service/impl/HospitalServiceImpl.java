package com.codesniper.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.codesniper.repository.HospitalRepository;
import com.codesniper.service.HospitalService;
import com.codesniper.yygh.model.hosp.Hospital;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.Map;

/**
 * 上传医院ServiceImpl
 *
 * @author CodeSniper
 * @since 2022/6/14 21:42
 */
@Service("HospitalService")
public class HospitalServiceImpl implements HospitalService {

    @Autowired
    private HospitalRepository hospitalRepository;

    @Override
    public void save(Map<String, Object> map) {
        // map转成对象Hospital
        String str = JSONObject.toJSONString(map);
        Hospital hospital = JSONObject.parseObject(str, Hospital.class);
        String hoscode = hospital.getHoscode();
        Hospital curHospital = hospitalRepository.getHospitalByHoscode(hoscode);

        // 如果存在,进行修改
        if (curHospital != null) {
            hospital.setStatus(curHospital.getStatus());
            hospital.setCreateTime(curHospital.getCreateTime());
            hospital.setUpdateTime(new Date());
            hospital.setIsDeleted(0);
            hospitalRepository.save(hospital);
        }else {
            // 不存在,进行添加
            hospital.setStatus(0);
            hospital.setCreateTime(new Date());
            hospital.setUpdateTime(new Date());
            hospital.setIsDeleted(0);
            hospitalRepository.save(hospital);
        }
    }
}
