package com.codesniper.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.codesniper.common.exception.MyException;
import com.codesniper.common.helper.JwtHelper;
import com.codesniper.common.result.ResultCodeEnum;
import com.codesniper.mapper.UserInfoMapper;
import com.codesniper.service.PatientService;
import com.codesniper.service.UserInfoService;
import com.codesniper.yygh.enums.AuthStatusEnum;
import com.codesniper.yygh.model.user.Patient;
import com.codesniper.yygh.model.user.UserInfo;
import com.codesniper.yygh.vo.user.LoginVo;
import com.codesniper.yygh.vo.user.UserAuthVo;
import com.codesniper.yygh.vo.user.UserInfoQueryVo;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 用户信息ServiceImpl
 *
 * @author CodeSniper
 * @since 2022/7/1 23:45
 */
@Service("UserInfoService")
public class UserInfoServiceImpl extends ServiceImpl<UserInfoMapper, UserInfo> implements UserInfoService {

    @Autowired
    private UserInfoMapper userInfoMapper;

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    @Autowired
    private PatientService patientService;

    @Override
    public Map<String, Object> login(LoginVo loginVo) {
        // 获取手机号,验证码
        String phone = loginVo.getPhone();
        String code = loginVo.getCode();

        // 校验手机号,验证码是否为空
        if (StringUtils.isEmpty(phone) || StringUtils.isEmpty(code)) {
            throw new MyException(ResultCodeEnum.PARAM_ERROR);
        }

        // 查询redis校验验证码是否一致
        String redisPhoneCode = redisTemplate.opsForValue().get(phone);
        if (!StringUtils.equals(code, redisPhoneCode)) {
            throw new MyException(ResultCodeEnum.CODE_ERROR);
        }

        // 绑定手机号
        UserInfo userInfo = null;
        if (StringUtils.isNotEmpty(loginVo.getOpenid())) {
            userInfo = this.getUserInfoByOpenId(loginVo.getOpenid());
            baseMapper.updateById(userInfo);
        } else {
            throw new MyException(ResultCodeEnum.DATA_ERROR);
        }

        // userInfo为空,正常进行手机登录
        if (userInfo == null) {
            // 查询是否存在该用户
            QueryWrapper<UserInfo> wrapper = new QueryWrapper<>();
            wrapper.eq("phone", phone);
            userInfo = baseMapper.selectOne(wrapper);
            if (userInfo == null) {
                // 第一次登录,添加到数据库
                userInfo = new UserInfo();
                userInfo.setName("");
                userInfo.setPhone(phone);
                userInfo.setStatus(1);
                baseMapper.insert(userInfo);
            }
        }

        // 校验是否禁用
        if (userInfo.getStatus() == 0) {
            throw new MyException(ResultCodeEnum.LOGIN_DISABLED_ERROR);
        }

        HashMap<String, Object> map = new HashMap<>();
        String name = userInfo.getName();
        if (StringUtils.isEmpty(name)) {
            name = userInfo.getNickName();
        }
        if (StringUtils.isEmpty(userInfo.getNickName())) {
            name = userInfo.getPhone();
        }

        // 生成token
        String token = JwtHelper.createToken(userInfo.getId(), userInfo.getName());

        // 封装返回信息
        map.put("name", name);
        map.put("token", token);
        return map;
    }

    @Override
    public UserInfo getUserInfoByOpenId(String openId) {
        QueryWrapper<UserInfo> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("openid", openId);
        return baseMapper.selectOne(queryWrapper);
    }

    @Override
    @Transactional
    public Boolean userAuth(Long currentUserId, UserAuthVo userAuthVo) {
        // 根据userId查询用户信息
        UserInfo userInfo = baseMapper.selectById(currentUserId);
        // 设置认证信息
        userInfo.setName(userAuthVo.getName());
        userInfo.setCertificatesType(userAuthVo.getCertificatesType());
        userInfo.setCertificatesNo(userAuthVo.getCertificatesNo());
        userInfo.setCertificatesUrl(userAuthVo.getCertificatesUrl());
        userInfo.setAuthStatus(AuthStatusEnum.AUTH_RUN.getStatus());
        // 更新信息
        int i = baseMapper.updateById(userInfo);
        return i == 1;
    }

    @Override
    public IPage<UserInfo> getUserList(UserInfoQueryVo userInfoQueryVo) {
        // 用户名称
        String name = userInfoQueryVo.getKeyword();
        // 用户状态
        Integer status = userInfoQueryVo.getStatus();
        // 认证状态
        Integer authStatus = userInfoQueryVo.getAuthStatus();
        // 开始时间
        String createTimeBegin = userInfoQueryVo.getCreateTimeBegin();
        // 结束时间
        String createTimeEnd = userInfoQueryVo.getCreateTimeEnd();

        QueryWrapper<UserInfo> queryWrapper = new QueryWrapper<>();
        queryWrapper.like(StringUtils.isNoneEmpty(name), "name", name);
        queryWrapper.eq(status != null, "status", status);
        queryWrapper.eq(authStatus != null, "auth_status", authStatus);
        queryWrapper.ge(StringUtils.isNotEmpty(createTimeBegin), "create_time", createTimeBegin);
        queryWrapper.le(StringUtils.isNotEmpty(createTimeEnd), "create_time", createTimeEnd);
        IPage<UserInfo> userInfoPage = baseMapper.selectPage(new Page<>(userInfoQueryVo.getPageNum(), userInfoQueryVo.getPageSize()), queryWrapper);

        userInfoPage.getRecords().forEach(this::packageUserInfo);
        return userInfoPage;
    }

    @Override
    public Boolean lockAndUnLock(UserInfoQueryVo userInfoQueryVo) {
        if (userInfoQueryVo.getStatus() == 0 || userInfoQueryVo.getStatus() == 1) {
            UserInfo userInfo = baseMapper.selectById(userInfoQueryVo.getUserId());
            userInfo.setStatus(userInfoQueryVo.getStatus());
            baseMapper.updateById(userInfo);
            return true;
        }
        return false;
    }

    @Override
    public Map<String, Object> getUserDetail(Long userId) {
        // 用户信息
        UserInfo userInfo = baseMapper.selectById(userId);
        this.packageUserInfo(userInfo);
        // 就诊人信息
        List<Patient> patientList = patientService.getPatientList(userId);
        HashMap<String, Object> map = new HashMap<>();
        map.put("userInfo", userInfo);
        map.put("patientList", patientList);
        return map;
    }

    @Override
    public boolean approval(UserInfoQueryVo userInfoQueryVo) {
        if (userInfoQueryVo.getAuthStatus()==2 || userInfoQueryVo.getAuthStatus() == -1) {
            UserInfo userInfo = baseMapper.selectById(userInfoQueryVo.getUserId());
            userInfo.setAuthStatus(userInfoQueryVo.getAuthStatus());
            baseMapper.updateById(userInfo);
            return true;
        }
        return false;
    }

    private void packageUserInfo(UserInfo userInfo) {
        // 认证状态编码
        userInfo.getParam().put("authStatusString", AuthStatusEnum.getStatusNameByStatus(userInfo.getAuthStatus()));
        // 用户状态
        String statusString = userInfo.getStatus() == 0 ? "锁定" : "正常";
        userInfo.getParam().put("statusString", statusString);
    }
}
