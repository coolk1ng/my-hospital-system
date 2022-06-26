package com.codesniper.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.codesniper.repository.ScheduleRepository;
import com.codesniper.service.DepartmentService;
import com.codesniper.service.HospitalService;
import com.codesniper.service.ScheduleService;
import com.codesniper.yygh.model.hosp.Schedule;
import com.codesniper.yygh.vo.hosp.BookingScheduleRuleVo;
import com.codesniper.yygh.vo.hosp.ScheduleQueryVo;
import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;
import org.joda.time.DateTimeConstants;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
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

    @Autowired
    private MongoTemplate mongoTemplate;

    @Autowired
    private HospitalService hospitalService;

    @Autowired
    private DepartmentService departmentService;

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
        if (schedule != null) {
            scheduleRepository.deleteById(schedule.getId());
        }
    }

    @Override
    public Map<String, Object> getScheduleRule(ScheduleQueryVo scheduleQueryVo) {
        // 根据医院编号和科室编号查询
        Criteria criteria = Criteria.where("hoscode").is(scheduleQueryVo.getHoscode()).and("depcode").is(scheduleQueryVo.getDepcode());

        // 根据工作日进行分组
        Aggregation aggregation = Aggregation.newAggregation(
                Aggregation.match(criteria),
                Aggregation.group("workDate").first("workDate").as("workDate")
                        .count().as("docCount")
                        .sum("reservedNumber").as("reservedNumber")
                        .sum("availableNumber").as("availableNumber"),
                Aggregation.sort(Sort.Direction.DESC, "workDate"),
                Aggregation.skip((scheduleQueryVo.getPageNum() - 1) * scheduleQueryVo.getPageSize()),
                Aggregation.limit(scheduleQueryVo.getPageSize())
        );

        // 查询结果
        List<BookingScheduleRuleVo> results = mongoTemplate.aggregate(aggregation, Schedule.class, BookingScheduleRuleVo.class).getMappedResults();

        // 查询总记录数条件
        Aggregation totalByWorkDate = Aggregation.newAggregation(
                Aggregation.match(criteria),
                Aggregation.group("workDate")
        );

        // 总记录数
        int total = mongoTemplate.aggregate(totalByWorkDate, Schedule.class, BookingScheduleRuleVo.class).getMappedResults().size();

        // 获取日期对应星期几
        for (BookingScheduleRuleVo result : results) {
            String dayOfWeek = this.getDayOfWeek(new DateTime(result.getWorkDate()));
            result.setDayOfWeek(dayOfWeek);
        }

        // 分装最终数据
        HashMap<String, Object> map = new HashMap<>();
        map.put("results", results);
        map.put("total", total);

        // 其他基础数据
        HashMap<String, Object> baseMap = new HashMap<>();
        String hosname = hospitalService.getHospitalByHoscode(scheduleQueryVo.getHoscode()).getHosname();
        baseMap.put("hosname", StringUtils.isBlank(hosname) ? "" : hosname);
        map.put("baseMap", baseMap);
        return map;
    }

    @Override
    public List<Schedule> getScheduleDetail(ScheduleQueryVo scheduleQueryVo) {
        List<Schedule> scheduleList = scheduleRepository.findScheduleByHoscodeAndDepcodeAndWorkDate(scheduleQueryVo.getHoscode(),
                scheduleQueryVo.getDepcode(), new DateTime(scheduleQueryVo.getWorkDate()).toDate());
        for (Schedule schedule : scheduleList) {
            // 设置医院名称
            schedule.getParam().put("hosname",hospitalService.getHospitalByHoscode(schedule.getHoscode()).getHosname());

            // 设置科室名称
            schedule.getParam().put("depname",departmentService.getDepartmentByHoscodeAndDepcode(schedule.getHoscode(), schedule.getDepcode()).getDepname());

            // 设置日期对应星期
            schedule.getParam().put("dayOfWeek",this.getDayOfWeek(new DateTime(schedule.getWorkDate())));
        }
        return scheduleList;
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

    /**
     * 根据日期获取周几
     *
     * @param dateTime
     * @return String
     */
    private String getDayOfWeek(DateTime dateTime) {
        String dayOfWeek = "";
        switch (dateTime.getDayOfWeek()) {
            case DateTimeConstants.SUNDAY:
                dayOfWeek = "周日";
                break;
            case DateTimeConstants.MONDAY:
                dayOfWeek = "周一";
                break;
            case DateTimeConstants.TUESDAY:
                dayOfWeek = "周二";
                break;
            case DateTimeConstants.WEDNESDAY:
                dayOfWeek = "周三";
                break;
            case DateTimeConstants.THURSDAY:
                dayOfWeek = "周四";
                break;
            case DateTimeConstants.FRIDAY:
                dayOfWeek = "周五";
                break;
            case DateTimeConstants.SATURDAY:
                dayOfWeek = "周六";
            default:
                break;
        }
        return dayOfWeek;
    }

    public static void main(String[] args) {
        System.out.println(new Date());
    }
}
