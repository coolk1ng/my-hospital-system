package com.codesniper.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.codesniper.repository.ScheduleRepository;
import com.codesniper.service.ScheduleService;
import com.codesniper.yygh.model.hosp.Schedule;
import com.codesniper.yygh.vo.hosp.ScheduleQueryVo;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.Map;

/**
 * 排班ServiceImpl
 *
 * @author CodeSniper
 * @since 2022/6/22 16:34
 */
@Service("ScheduleService")
public class ScheduleServiceImpl implements ScheduleService {

    @Autowired
    private ScheduleRepository scheduleRepository;

    @Override
    public void save(Map<String, Object> map) {
        String mapString = JSONObject.toJSONString(map);
        Schedule schedule = JSONObject.parseObject(mapString, Schedule.class);

        // 根据hoscode,depcode查询排班信息
        Schedule scheduleExist = scheduleRepository.getScheduleByHoscodeAndHosScheduleId(schedule.getHoscode(), schedule.getHosScheduleId());

        if (scheduleExist == null) {
            schedule.setCreateTime(new Date());
        }
        schedule.setUpdateTime(new Date());
        schedule.setIsDeleted(0);
        schedule.setStatus(1);
        scheduleRepository.save(schedule);
    }

    @Override
    public void remove(String hoscode, String hosScheduleId) {
        Schedule schedule = scheduleRepository.getScheduleByHoscodeAndHosScheduleId(hoscode, hosScheduleId);
        if (schedule!=null) {
            scheduleRepository.deleteById(schedule.getId());
        }
    }

    @Override
    public Page<Schedule> findPageSchedule(Integer page, Integer limit, ScheduleQueryVo scheduleQueryVo) {
        Schedule schedule = new Schedule();
        BeanUtils.copyProperties(scheduleQueryVo, schedule);
        PageRequest pageRequest = PageRequest.of(page - 1, limit);
        ExampleMatcher exampleMatcher = ExampleMatcher.matching()
                .withStringMatcher(ExampleMatcher.StringMatcher.CONTAINING)
                .withIgnoreCase(true);
        Example<Schedule> example = Example.of(schedule, exampleMatcher);
        return scheduleRepository.findAll(example, pageRequest);
    }
}
