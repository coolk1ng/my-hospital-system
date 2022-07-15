package com.codesniper.repository;

import com.codesniper.yygh.model.hosp.Schedule;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

/**
 * 排班
 *
 * @author CodeSniper
 * @since 2022/6/22 16:33
 */
@Repository
public interface ScheduleRepository extends MongoRepository<Schedule,String> {
    /** 
     * 根据医院编号和排班编号查询
     * @param hoscode
     * @param hosScheduleId 
     * @return Schedule
     */
    Schedule getScheduleByHoscodeAndHosScheduleId(String hoscode, String hosScheduleId);

    /**
     * 根据hoscode,depcode,workDate查询排班详情
     * @param hoscode
     * @param depcode
     * @param workDate
     * @return void
     */
    List<Schedule> findScheduleByHoscodeAndDepcodeAndWorkDate(String hoscode, String depcode, Date workDate);
}
