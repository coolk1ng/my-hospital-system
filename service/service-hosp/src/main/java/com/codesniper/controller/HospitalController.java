package com.codesniper.controller;

import com.codesniper.common.result.Result;
import com.codesniper.service.HospitalService;
import com.codesniper.yygh.model.hosp.Hospital;
import com.codesniper.yygh.vo.hosp.HospitalQueryVo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

/**
 * 医院信息Controller
 *
 * @author CodeSniper
 * @since 2022/6/22 23:21
 */
@RestController
@RequestMapping("/admin/hosp/hospital")
@Api(tags = "医院接口")
@CrossOrigin
public class HospitalController {

    @Autowired
    private HospitalService hospitalService;

    @ApiOperation("查询医院列表")
    @PostMapping("/getHospitalList")
    public Result<Page<Hospital>> getHospitalList(@RequestBody HospitalQueryVo hospitalQueryVo) {
        Page<Hospital> hospitalPage = hospitalService.getHospitalList(hospitalQueryVo);
        return Result.ok(hospitalPage);
    }
}
