package com.codesniper.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.codesniper.yygh.model.order.RefundInfo;
import org.apache.ibatis.annotations.Mapper;

/**
 * 退款
 *
 * @author CodeSniper
 * @since 2022/8/1 23:59
 */
@Mapper
public interface RefundInfoMapper extends BaseMapper<RefundInfo> {
}
