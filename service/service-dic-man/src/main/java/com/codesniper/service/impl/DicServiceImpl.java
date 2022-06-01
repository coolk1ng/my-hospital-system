package com.codesniper.service.impl;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.codesniper.mapper.DictMapper;
import com.codesniper.service.DicService;
import com.codesniper.yygh.model.dict.Dict;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 字典项ServiceImpl
 *
 * @author CodeSniper
 * @since 2022-06-02
 */
@Service("DicService")
public class DicServiceImpl extends ServiceImpl<DictMapper, Dict> implements DicService {

    @Override
    public List<Dict> getChildrenData(Long id) {
        QueryWrapper<Dict> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("parent_id", id);
        List<Dict> list = baseMapper.selectList(queryWrapper);
        for (Dict dict : list) {
            Boolean hasChildren = this.isHasChildren(dict.getId());
            dict.setHasChildren(hasChildren);
        }
        return list;
    }

    /**
     * 判断是否有子数据
     * @param id
     * @return Boolean
     */
    public Boolean isHasChildren(Long id) {
        QueryWrapper<Dict> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("parent_id",id);
        Integer count = baseMapper.selectCount(queryWrapper);
        return count > 0;
    }
}
