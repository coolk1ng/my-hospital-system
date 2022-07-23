package com.codesniper.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.codesniper.client.DictFeignClient;
import com.codesniper.mapper.PatientMapper;
import com.codesniper.service.PatientService;
import com.codesniper.yygh.enums.DictEnum;
import com.codesniper.yygh.model.user.Patient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 就诊人ServiceImpl
 *
 * @author CodeSniper
 * @since 2022/7/8 20:50
 */
@Service("PatientService")
public class PatientServiceImpl extends ServiceImpl<PatientMapper, Patient> implements PatientService {

    @Autowired
    private PatientMapper patientMapper;

    @Autowired
    private DictFeignClient dictFeignClient;

    @Autowired
    private PatientService patientService;

    @Override
    public List<Patient> getPatientList(Long userId) {
        QueryWrapper<Patient> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_id", userId);
        List<Patient> list = baseMapper.selectList(queryWrapper);

        // 设置字典数据
        list.forEach(this::packageData);
        return list;
    }

    @Override
    @Transactional
    public Patient getPatientById(Long id) {

        Patient patient = baseMapper.selectById(id);
        // 证件类型
        String certificatesType = dictFeignClient.getDictName(DictEnum.CERTIFICATES_TYPE.getDictCode(), patient.getCertificatesType());
        // 联系人证件类型
        String contactsCertificatesType = dictFeignClient.getDictName(DictEnum.CERTIFICATES_TYPE.getDictCode(), patient.getContactsCertificatesType());
        // 省
        String province = dictFeignClient.getDictNameByValue(patient.getProvinceCode());
        // 市
        String city = dictFeignClient.getDictNameByValue(patient.getCityCode());
        // 区
        String district = dictFeignClient.getDictNameByValue(patient.getDistrictCode());
        patient.getParam().put("certificatesType", certificatesType);
        patient.getParam().put("contactsCertificatesType", contactsCertificatesType);
        patient.getParam().put("province", province);
        patient.getParam().put("city", city);
        patient.getParam().put("district", district);
        patient.getParam().put("fullAddress", province + city + district + patient.getAddress());
        return patient;
    }

    private Patient packageData(Patient patient) {
        // 证件类型
        String certificatesType = dictFeignClient.getDictName(DictEnum.CERTIFICATES_TYPE.getDictCode(), patient.getCertificatesType());
        // 联系人证件类型
        String contactsCertificatesType = dictFeignClient.getDictName(DictEnum.CERTIFICATES_TYPE.getDictCode(), patient.getContactsCertificatesType());
        // 省
        String province = dictFeignClient.getDictNameByValue(patient.getProvinceCode());
        // 市
        String city = dictFeignClient.getDictNameByValue(patient.getCityCode());
        // 区
        String district = dictFeignClient.getDictNameByValue(patient.getDistrictCode());
        patient.getParam().put("certificatesType", certificatesType);
        patient.getParam().put("contactsCertificatesType", contactsCertificatesType);
        patient.getParam().put("province", province);
        patient.getParam().put("city", city);
        patient.getParam().put("district", district);
        patient.getParam().put("fullAddress", province + city + district + patient.getAddress());
        return patient;
    }
}
