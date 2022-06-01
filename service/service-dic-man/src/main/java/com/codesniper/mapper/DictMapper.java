package com.codesniper.mapper;


import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.codesniper.yygh.model.dict.Dict;
import org.apache.ibatis.annotations.Mapper;

/**
 * 字典项Mapper
 *
 * @author CodeSniper
 * @since 2022-06-02
 */
@Mapper
public interface DictMapper extends BaseMapper<Dict> {
}
