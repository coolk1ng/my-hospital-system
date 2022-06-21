package com.codesniper.service;

import com.codesniper.yygh.model.hosp.Department;
import com.codesniper.yygh.vo.hosp.DepartmentQueryVo;
import org.springframework.data.domain.Page;

import java.util.Map;

/**
 * 科室管理Service
 *
 * @author CodeSniper
 * @since 2022/6/20 23:35
 */
public interface DepartmentService {
    /** 
     * 添加科室
     * @param map 
     * @return void
     */
    void save(Map<String, Object> map);

    /** 
     * 分页查询科室
     * @param page
     * @param limit
     * @param departmentQueryVo 
     * @return Page<Department>
     */
    Page<Department> getDepartmentPage(Integer page, Integer limit, DepartmentQueryVo departmentQueryVo);

    /** 
     * 删除科室
     * @param hoscode
     * @param depcode 
     * @return void
     */
    void remove(String hoscode, String depcode);
}
