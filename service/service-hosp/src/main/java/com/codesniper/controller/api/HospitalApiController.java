package com.codesniper.controller.api;

import com.codesniper.common.result.Result;
import com.codesniper.service.DepartmentService;
import com.codesniper.service.HospitalService;
import com.codesniper.service.ScheduleService;
import com.codesniper.yygh.model.hosp.Hospital;
import com.codesniper.yygh.model.hosp.Schedule;
import com.codesniper.yygh.vo.hosp.DepartmentVo;
import com.codesniper.yygh.vo.hosp.HospitalQueryVo;
import com.codesniper.yygh.vo.hosp.ScheduleQueryVo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 医院前台Controller
 *
 * @author CodeSniper
 * @since 2022/6/28 05:55
 */
@RestController
@RequestMapping("/api/hosp/hospital")
@Api(tags = "医院前台接口")
@Slf4j
public class HospitalApiController {

    @Autowired
    private HospitalService hospitalService;

    @Autowired
    private DepartmentService departmentService;

    @Autowired
    private ScheduleService scheduleService;

    @PostMapping("/getHospitalList")
    @ApiOperation("查询医院列表")
    public Result<Page<Hospital>> getHospitalList(@RequestBody HospitalQueryVo hospitalQueryVo) {
        return Result.ok(hospitalService.getHospitalList(hospitalQueryVo));
    }

    @PostMapping("/getHospitalByHosname")
    @ApiOperation("根据医院名称模糊查询")
    public Result<List<Hospital>> getHospitalByHosname(@RequestBody HospitalQueryVo hospitalQueryVo) {
        return Result.ok(hospitalService.getHospitalByHosname(hospitalQueryVo));
    }

    @GetMapping("/getHospitalByHoscode/{hoscode}")
    @ApiOperation("根据医院编号查询科室")
    public Result<List<DepartmentVo>> getHospitalByHoscode(@PathVariable String hoscode) {
        return Result.ok(departmentService.getDepartmentList(hoscode));
    }

    @GetMapping("/getScheduleDetailByHoscode/{hoscode}")
    @ApiOperation("根据医院编号查询预约挂号详情")
    public Result<Map<String, Object>> getScheduleDetailByHoscode(@PathVariable String hoscode) {
        return Result.ok(hospitalService.getScheduleDetailByHoscode(hoscode));
    }

    @ApiOperation(value = "获取可预约排班数据")
    @GetMapping("/auth/getBookingScheduleRule/{page}/{limit}/{hoscode}/{depcode}")
    public Result<Map<String, Object>> getBookingSchedule(@PathVariable Integer page,
                                                          @PathVariable Integer limit,
                                                          @PathVariable String hoscode,
                                                          @PathVariable String depcode) {
        return Result.ok(scheduleService.getBookingScheduleRule(page, limit, hoscode, depcode));
    }

    @ApiOperation(value = "获取排班数据")
    @GetMapping("auth/findScheduleList/{hoscode}/{depcode}/{workDate}")
    public Result<List<Schedule>> findScheduleList(
            @ApiParam(name = "hoscode", value = "医院code", required = true)
            @PathVariable String hoscode,
            @ApiParam(name = "depcode", value = "科室code", required = true)
            @PathVariable String depcode,
            @ApiParam(name = "workDate", value = "排班日期", required = true)
            @PathVariable String workDate) {
        ScheduleQueryVo scheduleQueryVo = new ScheduleQueryVo();
        scheduleQueryVo.setHoscode(hoscode);
        scheduleQueryVo.setDepcode(depcode);
        scheduleQueryVo.setWorkDate(workDate);
        return Result.ok(scheduleService.getScheduleDetail(scheduleQueryVo));
    }

    @ApiOperation("根据排班id获取排班数据")
    @GetMapping("/auth/getScheduleByScheduleId/{scheduleId}")
    public Result<Schedule> getScheduleByScheduleId(@PathVariable String scheduleId) {
        return Result.ok(scheduleService.getScheduleByScheduleId(scheduleId));
    }

}
