package com.codesniper.controller.api;

import com.codesniper.common.exception.MyException;
import com.codesniper.common.helper.HttpRequestHelper;
import com.codesniper.common.result.Result;
import com.codesniper.common.result.ResultCodeEnum;
import com.codesniper.common.utils.MD5;
import com.codesniper.service.HospitalService;
import com.codesniper.service.HospitalSetService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

/**
 * @author CodeSniper
 * @since 2022/6/14 21:43
 */
@Slf4j
@RestController
@RequestMapping("/api/hosp")
public class ApiController {

    @Autowired
    private HospitalService hospitalService;

    @Autowired
    private HospitalSetService hospitalSetService;

    @PostMapping("/saveHospital")
    public Result<Boolean> saveHospital(HttpServletRequest httpServletRequest) {
        // 获取医院传来的信息
        Map<String, String[]> parameterMap = httpServletRequest.getParameterMap();
        Map<String, Object> map = HttpRequestHelper.switchMap(parameterMap);

        // 获取医院系统传来的签名
        String sign = (String) map.get("sign");
        log.info("传入的sign" + sign);

        // 根据医院编码查询签名
        String hoscode = (String) map.get("hoscode");
        String signKey = hospitalSetService.getSignKey(hoscode);
        log.info("查询的未加密sign" + signKey);

        // 查询的signKey加密
        String encryptSignKey = MD5.encrypt(signKey);
        log.info("查询的加密sign" + encryptSignKey);

        // 比较是否一致,不一致抛出异常
        if (!StringUtils.equals(sign, encryptSignKey)) {
            throw new MyException(ResultCodeEnum.SIGN_ERROR);
        }

        hospitalService.save(map);
        return Result.ok();
    }
}
