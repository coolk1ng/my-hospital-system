package com.codesniper.service.impl;


import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.codesniper.mapper.DictMapper;
import com.codesniper.service.DicService;
import com.codesniper.yygh.model.dict.Dict;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 字典项ServiceImpl
 *
 * @author CodeSniper
 * @since 2022-06-02
 */
@Service("DicService")
public class DicServiceImpl extends ServiceImpl<DictMapper, Dict> implements DicService {

    @Autowired
    private DictMapper dictMapper;
}
