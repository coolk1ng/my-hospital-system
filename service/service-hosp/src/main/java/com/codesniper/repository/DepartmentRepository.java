package com.codesniper.repository;

import com.codesniper.yygh.model.hosp.Department;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

/**
 * 科室管理
 *
 * @author CodeSniper
 * @since 2022/6/20 23:34
 */
@Repository
public interface DepartmentRepository extends MongoRepository<Department,String> {
    //查询科室
    Department getDepartmentByHoscodeAndDepcode(String hoscode, String depcode);

}
