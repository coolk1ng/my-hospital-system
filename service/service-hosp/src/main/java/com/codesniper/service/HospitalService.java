package com.codesniper.service;

import com.codesniper.yygh.model.hosp.Hospital;
import com.codesniper.yygh.vo.hosp.HospitalQueryVo;
import org.springframework.data.domain.Page;

import java.util.Map;

/**
 * 上传医院Service
 *
 * @author CodeSniper
 * @since 2022/6/14 21:41
 */
public interface HospitalService {
    /**
     * 添加医院信息
     * @param map
     * @return void
     */
    void save(Map<String, Object> map);

    /**
     * 查询医院信息
     * @param hoscode
     * @return Hospital
     */
    Hospital getHospitalByHoscode(String hoscode);

    /** 
     * 查询医院列表
     * @param hospitalQueryVo 
     * @return Page<Hospital>
     */
    Page<Hospital> getHospitalList(HospitalQueryVo hospitalQueryVo);

    /**
     * 修改医院状态
     * @param hospital
     * @return void
     */
    void updateHospitalStatus(Hospital hospital);

    Map<String,Object> getHospitalDetail(String id);

}
