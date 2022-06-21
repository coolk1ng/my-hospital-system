package com.codesniper.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.codesniper.repository.DepartmentRepository;
import com.codesniper.service.DepartmentService;
import com.codesniper.yygh.model.hosp.Department;
import com.codesniper.yygh.vo.hosp.DepartmentQueryVo;
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
 * 科室管理ServiceImpl
 *
 * @author CodeSniper
 * @since 2022/6/20 23:35
 */
@Service("DepartmentService")
public class DepartmentServiceImpl implements DepartmentService {

    @Autowired
    private DepartmentRepository departmentRepository;

    @Override
    public void save(Map<String, Object> map) {
        String mapString = JSONObject.toJSONString(map);
        Department department = JSONObject.parseObject(mapString, Department.class);

        //查询科室信息
        Department departmentExist =  departmentRepository.getDepartmentByHoscodeAndDepcode(department.getHoscode(),department.getDepcode());

        // 存在编辑,不存在新增
        if (departmentExist == null) {
            department.setCreateTime(new Date());
        }
        department.setUpdateTime(new Date());
        department.setIsDeleted(0);
        departmentRepository.save(department);
    }

    @Override
    public Page<Department> getDepartmentPage(Integer page, Integer limit, DepartmentQueryVo departmentQueryVo) {
        Department department = new Department();
        BeanUtils.copyProperties(departmentQueryVo,department);
        PageRequest pageRequest = PageRequest.of(page, limit);
        ExampleMatcher exampleMatcher = ExampleMatcher.matching()
                .withStringMatcher(ExampleMatcher.StringMatcher.CONTAINING)
                .withIgnoreCase(true);
        Example<Department> example = Example.of(department, exampleMatcher);
        return departmentRepository.findAll(example, pageRequest);
    }

    @Override
    public void remove(String hoscode, String depcode) {
        Department department = departmentRepository.getDepartmentByHoscodeAndDepcode(hoscode, depcode);
        if (department !=null) {
            departmentRepository.deleteById(department.getId());
        }
    }
}
