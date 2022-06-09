package com.hao.service.truck.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hao.mapper.truck.TruckTypeMapper;
import com.hao.pojo.truck.TruckType;
import com.hao.service.truck.ITruckTypeService;
import org.springframework.stereotype.Service;

@Service
public class TruckTypeServiceImpl extends ServiceImpl<TruckTypeMapper,TruckType> implements ITruckTypeService {

}
