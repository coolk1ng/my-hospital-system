package com.codesniper.service.impl;


import com.alibaba.excel.EasyExcel;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.codesniper.listener.DictListener;
import com.codesniper.mapper.DictMapper;
import com.codesniper.service.DicService;
import com.codesniper.yygh.model.dict.Dict;
import com.codesniper.yygh.vo.cmn.DictEeVo;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
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

    @Override
    public void exportDictData(HttpServletResponse httpServletResponse) {
        //设置下载信息
        httpServletResponse.setContentType("application/vnd.ms-excel");
        httpServletResponse.setCharacterEncoding("utf-8");
        String fileName = "dict";
        httpServletResponse.setHeader("Content-disposition", "attachment;filename=" + fileName + ".xlsx");
        //查询数据
        List<Dict> dictList = baseMapper.selectList(null);
        ArrayList<DictEeVo> exportList = new ArrayList<>();
        for (Dict dict : dictList) {
            DictEeVo exportData = new DictEeVo();
            BeanUtils.copyProperties(dict, exportData);
            exportList.add(exportData);
        }

        //写入
        try {
            EasyExcel.write(httpServletResponse.getOutputStream(), DictEeVo.class).sheet("dict").doWrite(exportList);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void importDictData(MultipartFile file) {
        try {
            EasyExcel.read(file.getInputStream(), DictEeVo.class, new DictListener(baseMapper)).sheet().doRead();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 判断是否有子数据
     *
     * @param id
     * @return Boolean
     */
    public Boolean isHasChildren(Long id) {
        QueryWrapper<Dict> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("parent_id", id);
        Integer count = baseMapper.selectCount(queryWrapper);
        return count > 0;
    }
}