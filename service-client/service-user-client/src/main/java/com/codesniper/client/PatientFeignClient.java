package com.codesniper.client;

import com.codesniper.yygh.model.user.Patient;
import com.codesniper.yygh.vo.feign.FeignVo;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Repository;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

/**
 * @author CodeSniper
 * @since 2022/7/17 20:29
 */
@Repository
@FeignClient("service-user")
public interface PatientFeignClient {

    /**
     * 获取就诊人信息
     * @param feignVo
     * @return Patient
     */
    @PostMapping("/api/user/patient/inner/getPatientById")
    public Patient getPatient(@RequestBody FeignVo feignVo);
}
