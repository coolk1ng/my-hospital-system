package com.codesniper.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.codesniper.common.exception.MyException;
import com.codesniper.common.helper.JwtHelper;
import com.codesniper.common.result.ResultCodeEnum;
import com.codesniper.mapper.UserInfoMapper;
import com.codesniper.service.UserInfoService;
import com.codesniper.yygh.model.user.UserInfo;
import com.codesniper.yygh.vo.user.LoginVo;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.HashMap;
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
    private RedisTemplate<String,String> redisTemplate;

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
        String redisPhone = redisTemplate.opsForValue().get(phone);
        if (!StringUtils.equals(phone,redisPhone)) {
            throw new MyException(ResultCodeEnum.CODE_ERROR);
        }

        // 查询是否存在该用户
        QueryWrapper<UserInfo> wrapper = new QueryWrapper<>();
        wrapper.eq("phone", phone);
        UserInfo userInfo = baseMapper.selectOne(wrapper);
        if (userInfo == null) {
            // 第一次登录,添加到数据库
            userInfo = new UserInfo();
            userInfo.setName("");
            userInfo.setPhone(phone);
            userInfo.setStatus(1);
            baseMapper.insert(userInfo);
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
        map.put("name",name);
        map.put("token",token);
        return map;
    }
}
