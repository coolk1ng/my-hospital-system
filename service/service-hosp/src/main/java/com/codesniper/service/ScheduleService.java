package com.codesniper.service;

import com.codesniper.yygh.model.hosp.Schedule;
import com.codesniper.yygh.vo.hosp.ScheduleQueryVo;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.Map;

/**
 * 排班Service
 *
 * @author CodeSniper
 * @since 2022/6/22 16:34
 */
public interface ScheduleService {
    /** 
     * 上传排班信息
     * @param map 
     * @return void
     */
    void save(Map<String, Object> map);

    /** 
     * 分页查询
     * @param page
     * @param limit
     * @param scheduleQueryVo 
     * @return Page<Schedule>
     */
    Page<Schedule> findPageSchedule(Integer page, Integer limit, ScheduleQueryVo scheduleQueryVo);

    /** 
     * 删除
     * @param hoscode
     * @param hosScheduleId 
     * @return void
     */
    void remove(String hoscode, String hosScheduleId);

    /** 
     * 查询排班规则数据
     * @param scheduleQueryVo 
     * @return Map<String,Object>
     */
    Map<String, Object> getScheduleRule(ScheduleQueryVo scheduleQueryVo);

    /** 
     * 查询排班详情
     * @param scheduleQueryVo 
     * @return List<Schedule>
     */
    List<Schedule> getScheduleDetail(ScheduleQueryVo scheduleQueryVo);

    /**
     * 获取可预约的排班信息
     * @param scheduleQueryVo
     * @return Map<String,Object>
     */
    Map<String,Object> getBookingScheduleRule(ScheduleQueryVo scheduleQueryVo);
}
