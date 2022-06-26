package com.codesniper.controller;

import com.codesniper.common.result.Result;
import com.codesniper.service.DepartmentService;
import com.codesniper.yygh.vo.hosp.DepartmentVo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 科室Controller
 *
 * @author CodeSniper
 * @since 2022/6/25 21:42
 */
@RestController
@RequestMapping("/admin/hosp/department")
@Api(tags = "科室信息")
public class DepartmentController {

    @Autowired
    private DepartmentService departmentService;

    @GetMapping("/getDepartmentList/{hoscode}")
    @ApiOperation("获取科室列表")
    public Result<List<DepartmentVo>> getDepartmentList(@PathVariable String hoscode) {
        List<DepartmentVo> departmentList = departmentService.getDepartmentList(hoscode);
        return Result.ok(departmentList);
    }
}
