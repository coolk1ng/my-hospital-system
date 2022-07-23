package com.codesniper.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.codesniper.yygh.model.hosp.Schedule;
import com.codesniper.yygh.vo.hosp.ScheduleOrderVo;
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
public interface ScheduleService extends IService<Schedule> {
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

    Map<String,Object> getBookingScheduleRule(int page, int limit, String hoscode, String depcode);

    /**
     * 根据排班id查询排班数据
     * @param scheduleId
     * @return Schedule
     */
    Schedule getScheduleByHosScheduleId(String scheduleId);

    /**
     * 根据排班id查询预约下单信息
     * @param scheduleId
     * @return ScheduleOrderVo
     */
    ScheduleOrderVo getScheduleOrder(String scheduleId);

    /** 
     * 更新mq排班信息
     * @param schedule 
     * @return void
     */
    void update(Schedule schedule);
}
