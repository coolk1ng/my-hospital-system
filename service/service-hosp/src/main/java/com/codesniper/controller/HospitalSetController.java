package com.codesniper.controller;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.codesniper.common.result.Result;
import com.codesniper.common.utils.MD5;
import com.codesniper.service.HospitalSetService;
import com.codesniper.yygh.model.hosp.HospitalSet;
import com.codesniper.yygh.vo.hosp.HospitalSetQueryVo;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Random;

/**
 * 医院设置Controller
 *
 * @author CodeSniper
 * @since 2022-05-29
 */
@RestController
@RequestMapping("/admin/hosp/hospitalSet")
@Api(tags = "医院设置管理")
public class HospitalSetController {

    @Autowired
    private HospitalSetService hospitalSetService;

    @ApiOperation("获取所有医院设置")
    @PostMapping("/getAllHospitalSet")
    public Result<PageInfo<HospitalSet>> getAllHospitalSet(@RequestBody(required = false) HospitalSetQueryVo dto) {
        PageHelper.startPage(dto.getPageNum() == null ? 1 : dto.getPageNum(), dto.getPageSize() == null ? 10 : dto.getPageSize());
        //封装查询参数
        QueryWrapper<HospitalSet> queryWrapper = new QueryWrapper<>();
        queryWrapper.like(StringUtils.isNotBlank(dto.getHosname()), "hosname", dto.getHosname());
        queryWrapper.eq(StringUtils.isNotBlank(dto.getHoscode()), "hoscode", dto.getHoscode());
        PageInfo<HospitalSet> pageInfo = new PageInfo<>(hospitalSetService.list(queryWrapper));
        return Result.ok(pageInfo);
    }

    @ApiOperation("删除医院设置")
    @PostMapping("/deleteHospitalSetById")
    public Result<Boolean> deleteHospitalSetById(Long id) {
        boolean flag = hospitalSetService.removeById(id);
        if (flag) {
            return Result.ok();
        } else {
            return Result.fail();
        }
    }

    @ApiOperation("添加医院设置")
    @PostMapping("/saveHospitalSet")
    public Result<Boolean> saveHospitalSet(@RequestBody HospitalSet hospitalSet) {
        //设置状态 1: 使用,0: 不能使用
        hospitalSet.setStatus(1);
        //设置签名秘钥
        String signKey = MD5.encrypt(System.currentTimeMillis() + new Random().nextInt(1000) + "");
        hospitalSet.setSignKey(signKey);
        boolean flag = hospitalSetService.save(hospitalSet);
        if (flag){
            return Result.ok();
        }else{
            return Result.fail();
        }
    }

    @ApiOperation("根据id查询医院设置")
    @PostMapping("/getHospitalSetById")
    public Result<HospitalSet> getHospitalSetById(Long id){
        return Result.ok(hospitalSetService.getById(id));
    }

    @ApiOperation("修改医院设置")
    @PostMapping("/updateHospitalSet")
    public Result<Boolean> updateHospitalSet(@RequestBody HospitalSet hospitalSet){
        boolean flag = hospitalSetService.updateById(hospitalSet);
        if (flag){
            return Result.ok();
        }else{
            return Result.fail();
        }
    }

    @ApiOperation("批量删除医院设置")
    @PostMapping("/batchRemoveHospitalSet")
    public Result<Boolean> batchRemoveHospitalSet(@RequestBody List<Long> ids){
        hospitalSetService.removeByIds(ids);
        return Result.ok();
    }

    @ApiOperation("锁定,解锁医院设置")
    @PostMapping("/lockAndUnLockHospitalSet")
    public Result<Boolean> lockAndUnLockHospitalSet(Long id,Integer status){
        HospitalSet hospitalSet = hospitalSetService.getById(id);
        //设置医院设置状态
        hospitalSet.setStatus(status);
        hospitalSetService.updateById(hospitalSet);
        return Result.ok();
    }

    @ApiOperation("发送签名,秘钥")
    @PostMapping("/sendSignAndKey")
    public Result<Boolean> sendSignAndKey(Long id){
        HospitalSet hospitalSet = hospitalSetService.getById(id);
        String signKey = hospitalSet.getSignKey();
        String hoscode = hospitalSet.getHoscode();
        //TODO 发送短信
        return Result.ok();
    }
}
