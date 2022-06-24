package com.codesniper.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.codesniper.client.DictFeignClient;
import com.codesniper.repository.HospitalRepository;
import com.codesniper.service.HospitalService;
import com.codesniper.yygh.model.hosp.Hospital;
import com.codesniper.yygh.vo.hosp.HospitalQueryVo;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
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

    @Autowired
    private DictFeignClient dictFeignClient;

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
        }else {
            // 不存在,进行添加
            hospital.setStatus(0);
            hospital.setCreateTime(new Date());
        }
        hospital.setUpdateTime(new Date());
        hospital.setIsDeleted(0);
        hospitalRepository.save(hospital);
    }

    @Override
    public Page<Hospital> getHospitalList(HospitalQueryVo hospitalQueryVo) {
        PageRequest pageRequest = PageRequest.of(hospitalQueryVo.getPageNum() -1, hospitalQueryVo.getPageSize());

        ExampleMatcher exampleMatcher = ExampleMatcher.matching()
                .withStringMatcher(ExampleMatcher.StringMatcher.CONTAINING)
                .withIgnoreCase(true);

        Hospital hospital = new Hospital();
        BeanUtils.copyProperties(hospitalQueryVo,hospital);

        Example<Hospital> example= Example.of(hospital, exampleMatcher);
        Page<Hospital> pages = hospitalRepository.findAll(example, pageRequest);

        pages.getContent().forEach(this::setHospitalHosType);
        return pages;
    }

    private Hospital setHospitalHosType(Hospital hospital) {
        String hostype = dictFeignClient.getDictName("Hostype", hospital.getHostype());
        // 查询省,市,地区
        String cityCode = dictFeignClient.getDictNameByValue(hospital.getCityCode());
        String provinceCode = dictFeignClient.getDictNameByValue(hospital.getProvinceCode());
        String districtCode = dictFeignClient.getDictNameByValue(hospital.getDistrictCode());

        // 放到map中
        hospital.getParam().put("hostype",hostype);
        hospital.getParam().put("fullAddress",provinceCode+cityCode+districtCode);
        return hospital;
    }

    @Override
    public Hospital getHospitalByHoscode(String hoscode) {
        return hospitalRepository.getHospitalByHoscode(hoscode);
    }
}
