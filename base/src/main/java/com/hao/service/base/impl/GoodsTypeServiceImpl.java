package com.hao.service.base.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.metadata.OrderItem;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hao.mapper.base.GoodsTypeMapper;
import com.hao.pojo.base.GoodsType;
import com.hao.service.base.IGoodsTypeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class GoodsTypeServiceImpl extends ServiceImpl<GoodsTypeMapper, GoodsType> implements IGoodsTypeService {

    @Autowired
    private GoodsTypeMapper goodsTypeMapper;

    @Override
    public GoodsType findById(String id) {
        return goodsTypeMapper.selectById(id);
    }

    @Override
    public List<GoodsType> findAll() {
        LambdaQueryWrapper<GoodsType> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(GoodsType::getStatus,1);
        return goodsTypeMapper.selectList(lambdaQueryWrapper);
    }

    @Override
    public IPage<GoodsType> findByPage(Integer page, Integer pageSize, String name, String truckTypeId, String truckTypeName) {
        Page<GoodsType> goodsTypePage = new Page<>(page, pageSize);
        goodsTypePage.addOrder(OrderItem.asc("id"));
        goodsTypePage.setRecords(goodsTypeMapper.findByPage(goodsTypePage,name,truckTypeId,truckTypeName));
        return goodsTypePage;
    }

}
