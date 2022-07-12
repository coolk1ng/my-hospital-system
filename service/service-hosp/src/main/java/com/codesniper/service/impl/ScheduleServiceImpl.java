package com.codesniper.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.codesniper.common.exception.MyException;
import com.codesniper.common.result.ResultCodeEnum;
import com.codesniper.repository.ScheduleRepository;
import com.codesniper.service.DepartmentService;
import com.codesniper.service.HospitalService;
import com.codesniper.service.ScheduleService;
import com.codesniper.yygh.model.hosp.BookingRule;
import com.codesniper.yygh.model.hosp.Department;
import com.codesniper.yygh.model.hosp.Hospital;
import com.codesniper.yygh.model.hosp.Schedule;
import com.codesniper.yygh.vo.hosp.BookingScheduleRuleVo;
import com.codesniper.yygh.vo.hosp.ScheduleQueryVo;
import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;
import org.joda.time.DateTimeConstants;
import org.joda.time.format.DateTimeFormat;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.*;
import java.util.stream.Collectors;

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
            schedule.getParam().put("hosname", hospitalService.getHospitalByHoscode(schedule.getHoscode()).getHosname());

            // 设置科室名称
            schedule.getParam().put("depname", departmentService.getDepartmentByHoscodeAndDepcode(schedule.getHoscode(), schedule.getDepcode()).getDepname());

            // 设置日期对应星期
            schedule.getParam().put("dayOfWeek", this.getDayOfWeek(new DateTime(schedule.getWorkDate())));
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

    @Override
    public Map<String, Object> getBookingScheduleRule(ScheduleQueryVo scheduleQueryVo) {
        HashMap<String, Object> map = new HashMap<>();
        // 获取预约规则
        Hospital hospital = hospitalService.getHospitalByHoscode(scheduleQueryVo.getHoscode());
        if (hospital == null) {
            throw new MyException(ResultCodeEnum.DATA_ERROR);
        }
        BookingRule bookingRule = hospital.getBookingRule();

        IPage<Date> page = this.getDateList(scheduleQueryVo, bookingRule);
        // 当前可预约日期
        List<Date> list = page.getRecords();

        // 获取可预约日期里面科室的剩余预约数
        Criteria criteria = Criteria.where("hoscode").is(scheduleQueryVo.getHoscode())
                .and("depcode").is(scheduleQueryVo.getDepcode())
                .and("wordDate").in(list);
        Aggregation aggregation = Aggregation.newAggregation(
                Aggregation.match(criteria),
                Aggregation.group("workDate").first("workDate").as("workDate")
                        .count().as("docCount")
                        .sum("availableNumber").as("availableNumber")
                        .sum("reservedNumber").as("reservedNumber")

        );
        AggregationResults<BookingScheduleRuleVo> aggregate = mongoTemplate.aggregate(aggregation, Schedule.class, BookingScheduleRuleVo.class);
        List<BookingScheduleRuleVo> results = aggregate.getMappedResults();

        // 合并数据
        Map<Date, BookingScheduleRuleVo> scheduleMap = new HashMap<>();
        if (!CollectionUtils.isEmpty(results)) {
            scheduleMap = results.stream().collect(Collectors.toMap(BookingScheduleRuleVo::getWorkDate, v -> v));
        }

        // 获取可预约排班规则
        ArrayList<BookingScheduleRuleVo> bookingScheduleRuleList = new ArrayList<>();
        for (Date date : list) {
            BookingScheduleRuleVo scheduleRuleVo = scheduleMap.get(date);
            if (scheduleRuleVo == null) {
                scheduleRuleVo = new BookingScheduleRuleVo();
                // 就诊医生人数
                scheduleRuleVo.setDocCount(0);
                // 设置无号
                scheduleRuleVo.setAvailableNumber(-1);
            }
            scheduleRuleVo.setWorkDate(date);
            scheduleRuleVo.setWorkDateMd(date);
            // 当前日期对应的星期
            String dayOfWeek = this.getDayOfWeek(new DateTime(date));
            scheduleRuleVo.setDayOfWeek(dayOfWeek);
            // 最后一条记录为即将预约,状态 0: 正常,1: 即将放号,-1: 当天停止挂号
            if (list.indexOf(date) == list.size() - 1 && scheduleQueryVo.getPageNum() == page.getPages()) {
                scheduleRuleVo.setStatus(1);
            }else {
                scheduleRuleVo.setStatus(0);
            }
            // 当天过了停号时间,不能预约
            if (list.indexOf(date) == 0 && scheduleQueryVo.getPageNum() == 1) {
                DateTime stopTime = this.getDateTime(new Date(), bookingRule.getStopTime());
                if (stopTime.isBeforeNow()) {
                    scheduleRuleVo.setStatus(-1);
                }
            }
            bookingScheduleRuleList.add(scheduleRuleVo);
        }

        //可预约日期规则数据
        map.put("bookingScheduleRuleList", bookingScheduleRuleList);
        map.put("total", page.getTotal());
        //其他基础数据
        Map<String, String> baseMap = new HashMap<>();
        //医院名称
        baseMap.put("hosname", hospitalService.getHospitalByHoscode(scheduleQueryVo.getHoscode()).getHosname());
        //科室
        Department department =departmentService.getDepartmentByHoscodeAndDepcode(scheduleQueryVo.getHoscode(), scheduleQueryVo.getDepcode());
        //大科室名称
        baseMap.put("bigname", department.getBigname());
        //科室名称
        baseMap.put("depname", department.getDepname());
        //月
        baseMap.put("workDateString", new DateTime().toString("yyyy年MM月"));
        //放号时间
        baseMap.put("releaseTime", bookingRule.getReleaseTime());
        //停号时间
        baseMap.put("stopTime", bookingRule.getStopTime());
        map.put("baseMap", baseMap);
        return map;
    }

    private IPage<Date> getDateList(ScheduleQueryVo scheduleQueryVo, BookingRule bookingRule) {
        Integer pageNum = scheduleQueryVo.getPageNum();
        Integer pageSize = scheduleQueryVo.getPageSize();
        // 获取当天放号时间
        DateTime releaseTime = this.getDateTime(new Date(), bookingRule.getReleaseTime());
        // 获取预约周期
        Integer cycle = bookingRule.getCycle();
        if (releaseTime.isBeforeNow()) {
            cycle++;
        }

        // 获取可预约所有日期
        ArrayList<Date> list = new ArrayList<>();
        for (int i = 0; i < cycle; i++) {
            DateTime curDateTime = new DateTime().plusDays(i);
            String dateTimeStr = curDateTime.toString("yyyy-MM-dd");
            list.add(new DateTime(dateTimeStr).toDate());
        }
        List<Date> pageDateList = new ArrayList<>();

        int start = (pageNum-1)*pageSize;
        int end = (pageNum-1)*pageSize+pageSize;
        if(end >list.size()) end = list.size();
        for (int i = start; i < end; i++) {
            pageDateList.add(list.get(i));
        }
        IPage<Date> iPage = new com.baomidou.mybatisplus.extension.plugins.pagination.Page(pageNum, 7, list.size());
        iPage.setRecords(pageDateList);

        return iPage;
    }

    /**
     * Date(yyyy-MM-dd)日期转成DateTime
     *
     * @param date
     * @param timeString
     * @return DateTime
     */
    private DateTime getDateTime(Date date, String timeString) {
        String dateTimeString = new DateTime(date).toString("yyyy-MM-dd") + " " + timeString;
        return DateTimeFormat.forPattern("yyyy-MM-dd HH:mm").parseDateTime(dateTimeString);
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
