package com.hao.service.impl;


import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hao.mapper.OrderLocationMapper;
import com.hao.pojo.OrderLocation;
import com.hao.service.IOrderLocationService;
import org.springframework.stereotype.Service;

/**
 * 位置信息服务实现
 */
@Service
public class OrderLocationServiceImpl extends ServiceImpl<OrderLocationMapper, OrderLocation> implements IOrderLocationService {

}