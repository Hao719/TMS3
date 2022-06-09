package com.hao.mapper;


import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.hao.pojo.Order;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Component;

/**
 * 订单 Mapper 接口
 */
@Component
@Mapper
public interface OrderMapper extends BaseMapper<Order> {
}
