package com.codesniper.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.codesniper.yygh.model.user.UserInfo;
import org.apache.ibatis.annotations.Mapper;

/**
 * 用户信息Mapper
 *
 * @author CodeSniper
 * @since 2022/7/1 23:43
 */
@Mapper
public interface UserInfoMapper extends BaseMapper<UserInfo> {
}
