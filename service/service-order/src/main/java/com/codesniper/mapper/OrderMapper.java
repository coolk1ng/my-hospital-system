package com.codesniper.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.codesniper.yygh.model.order.OrderInfo;
import org.apache.ibatis.annotations.Mapper;

/**
 * @author CodeSniper
 * @since 2022/7/18 00:30
 */
@Mapper
public interface OrderMapper extends BaseMapper<OrderInfo> {
}
