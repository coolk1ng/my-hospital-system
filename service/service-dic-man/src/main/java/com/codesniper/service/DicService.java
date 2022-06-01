package com.codesniper.service;


import com.baomidou.mybatisplus.extension.service.IService;
import com.codesniper.yygh.model.dict.Dict;

import java.util.List;

/**
 * 字典项Service
 *
 * @author CodeSniper
 * @since 2022-06-02
 */
public interface DicService extends IService<Dict> {

    /**
     * 根据id查询子数据
     * @param id
     * @return List<Dict>
     */
    List<Dict> getChildrenData(Long id);
}
