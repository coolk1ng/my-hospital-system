package com.codesniper.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.codesniper.yygh.model.user.Patient;

import java.util.List;

/**
 * 就诊人Service
 *
 * @author CodeSniper
 * @since 2022/7/8 20:50
 */
public interface PatientService extends IService<Patient> {
    /**
     * 查询就诊人列表
     * @param userId
     * @return List<Patient>
     */
    List<Patient> getPatientList(Long userId);

    /**
     * 根据id查询就诊人信息
     * @param id
     * @return Object
     */
    Patient getPatientById(Long id);
}
