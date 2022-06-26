package com.codesniper.controller;

import com.codesniper.common.result.Result;
import com.codesniper.service.ScheduleService;
import com.codesniper.yygh.model.hosp.Schedule;
import com.codesniper.yygh.vo.hosp.ScheduleQueryVo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 排班Controller
 *
 * @author CodeSniper
 * @since 2022/6/26 07:43
 */
@RestController
@RequestMapping("/admin/hosp/schedule")
@Api(tags = "排班信息")
public class ScheduleController {

    @Autowired
    private ScheduleService scheduleService;

    @PostMapping("/getScheduleRule")
    @ApiOperation("查询排班规则数据")
    public Result<Map<String,Object>> getScheduleRule(@RequestBody ScheduleQueryVo scheduleQueryVo) {
        Map<String,Object> map = scheduleService.getScheduleRule(scheduleQueryVo);
        return Result.ok(map);
    }

    @PostMapping("/getScheduleDetail")
    @ApiOperation("查询排班详情")
    public Result<List<Schedule>> getScheduleDetail(@RequestBody ScheduleQueryVo scheduleQueryVo) {
        List<Schedule> list = scheduleService.getScheduleDetail(scheduleQueryVo);
        return Result.ok(list);
    }

}
