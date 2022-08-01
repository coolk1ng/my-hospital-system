package com.codesniper.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.codesniper.yygh.model.order.PaymentInfo;
import org.apache.ibatis.annotations.Mapper;

/**
 * @author CodeSniper
 * @since 2022/7/29 08:50
 */
@Mapper
public interface PaymentMapper extends BaseMapper<PaymentInfo> {
}
