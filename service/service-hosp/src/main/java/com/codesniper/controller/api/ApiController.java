package com.codesniper.controller.api;

import com.codesniper.common.exception.MyException;
import com.codesniper.common.helper.HttpRequestHelper;
import com.codesniper.common.result.Result;
import com.codesniper.common.result.ResultCodeEnum;
import com.codesniper.common.utils.MD5;
import com.codesniper.service.DepartmentService;
import com.codesniper.service.HospitalService;
import com.codesniper.service.HospitalSetService;
import com.codesniper.yygh.model.hosp.Department;
import com.codesniper.yygh.model.hosp.Hospital;
import com.codesniper.yygh.vo.hosp.DepartmentQueryVo;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
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

    @Autowired
    private DepartmentService departmentService;

    @PostMapping("/hospital/show")
    public Result<Hospital> getHospital(HttpServletRequest httpServletRequest) {
        /*Map<String, String[]> parameterMap = httpServletRequest.getParameterMap();
        Map<String, Object> map = HttpRequestHelper.switchMap(parameterMap);

        // 获取医院编号
        String hoscode = (String)map.get("hoscode");

        // 获取医院系统传来的签名
        String sign = (String) map.get("sign");

        // 根据医院编码查询签名
        String signKey = hospitalSetService.getSignKey(hoscode);

        // 查询的signKey加密
        String encryptSignKey = MD5.encrypt(signKey);

        // 比较是否一致,不一致抛出异常
        if (!StringUtils.equals(sign, encryptSignKey)) {
            throw new MyException(ResultCodeEnum.SIGN_ERROR);
        }*/

        Map<String, Object> map = this.isCheckBySignKey(httpServletRequest);
        Hospital hospital = hospitalService.getHospitalByHoscode((String) map.get("hoscode"));
        return Result.ok(hospital);
    }

    @PostMapping("/saveHospital")
    public Result<Boolean> saveHospital(HttpServletRequest httpServletRequest) {
        // 获取医院传来的信息
        Map<String, String[]> parameterMap = httpServletRequest.getParameterMap();
        Map<String, Object> map = HttpRequestHelper.switchMap(parameterMap);

        // 传输过程中 "+" 变成了 "",需要转换
        String logoData = (String) map.get("logoData");
        logoData = logoData.replaceAll(" ", "+");
        map.put("logoData", logoData);

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

    @PostMapping("/saveDepartment")
    public Result<Boolean> saveDepartment(HttpServletRequest httpServletRequest) {
        /*Map<String, String[]> parameterMap = httpServletRequest.getParameterMap();
        Map<String, Object> map = HttpRequestHelper.switchMap(parameterMap);

        // 获取医院编号
        String hoscode = (String)map.get("hoscode");

        // 获取医院系统传来的签名
        String sign = (String) map.get("sign");

        // 根据医院编码查询签名
        String signKey = hospitalSetService.getSignKey(hoscode);

        // 查询的signKey加密
        String encryptSignKey = MD5.encrypt(signKey);

        // 比较是否一致,不一致抛出异常
        if (!StringUtils.equals(sign, encryptSignKey)) {
            throw new MyException(ResultCodeEnum.SIGN_ERROR);
        }*/
        Map<String, Object> map = this.isCheckBySignKey(httpServletRequest);

        departmentService.save(map);
        return Result.ok();
    }

    @PostMapping("/department/list")
    public Result<Page<Department>> getDepartmentList(HttpServletRequest httpServletRequest) {
        // 获取传递过来的科室信息
        Map<String, String[]> parameterMap = httpServletRequest.getParameterMap();
        Map<String, Object> map = HttpRequestHelper.switchMap(parameterMap);

        String hoscode =(String) map.get("hoscode");

        // 当前页和每页记录数
        Integer page = StringUtils.isEmpty((String) map.get("page")) ? 1 : Integer.parseInt((String) map.get("page"));
        Integer limit = StringUtils.isEmpty((String) map.get("limit")) ? 1 : Integer.parseInt((String) map.get("limit"));

        // 签名校验
        String sign = (String) map.get("sign");

        // 根据医院编码查询签名
        String signKey = hospitalSetService.getSignKey(hoscode);

        // 查询的signKey加密
        String encryptSignKey = MD5.encrypt(signKey);

        // 比较是否一致,不一致抛出异常
        if (!StringUtils.equals(sign, encryptSignKey)) {
            throw new MyException(ResultCodeEnum.SIGN_ERROR);
        }

        DepartmentQueryVo departmentQueryVo = new DepartmentQueryVo();
        departmentQueryVo.setHoscode(hoscode);

        Page<Department> pageModel = departmentService.getDepartmentPage(page,limit,departmentQueryVo);
        return Result.ok(pageModel);
    }

    @PostMapping("/department/remove")
    public Result<Boolean> removeDepartment(HttpServletRequest httpServletRequest) {
        Map<String, Object> map = this.isCheckBySignKey(httpServletRequest);
        String hoscode =(String) map.get("hoscode");
        String depcode =(String) map.get("depcode");
        departmentService.remove(hoscode,depcode);
        return Result.ok();
    }

    public Map<String, Object> isCheckBySignKey(HttpServletRequest httpServletRequest) {
        Map<String, String[]> parameterMap = httpServletRequest.getParameterMap();
        Map<String, Object> map = HttpRequestHelper.switchMap(parameterMap);

        // 获取医院编号
        String hoscode = (String) map.get("hoscode");

        // 获取医院系统传来的签名
        String sign = (String) map.get("sign");

        // 根据医院编码查询签名
        String signKey = hospitalSetService.getSignKey(hoscode);

        // 查询的signKey加密
        String encryptSignKey = MD5.encrypt(signKey);

        // 比较是否一致,不一致抛出异常
        if (!StringUtils.equals(sign, encryptSignKey)) {
            throw new MyException(ResultCodeEnum.SIGN_ERROR);
        }
        return map;
    }
}
