package com.codesniper.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.codesniper.yygh.model.user.Patient;
import org.apache.ibatis.annotations.Mapper;

/**
 * 就诊人Mapper
 *
 * @author CodeSniper
 * @since 2022/7/8 20:52
 */
@Mapper
public interface PatientMapper extends BaseMapper<Patient> {
}
