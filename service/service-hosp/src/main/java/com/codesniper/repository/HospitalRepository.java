package com.codesniper.repository;

import com.codesniper.yygh.model.hosp.Hospital;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

/**
 * @author CodeSniper
 * @since 2022/6/14 21:37
 */
@Repository
public interface HospitalRepository extends MongoRepository<Hospital,String> {

    // 判断数据是否存在
    Hospital getHospitalByHoscode(String hoscode);
}
