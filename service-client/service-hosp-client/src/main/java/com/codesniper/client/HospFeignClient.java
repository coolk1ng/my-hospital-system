package com.codesniper.client;

import com.codesniper.yygh.vo.hosp.ScheduleOrderVo;
import com.codesniper.yygh.vo.order.SignInfoVo;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Repository;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * @author CodeSniper
 * @since 2022/7/17 23:14
 */
@Repository
@FeignClient("service-hosp")
public interface HospFeignClient {

    @PostMapping("/api/hosp/hospital/inner/getScheduleOrder")
    public ScheduleOrderVo getScheduleOrder(@RequestParam("scheduleId") String scheduleId);

    @PostMapping("/api/hosp/hospital/inner/getSignInfo")
    public SignInfoVo getSignInfo(@RequestParam("hoscode") String hoscode);
}
