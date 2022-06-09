package com.hao.service.middle.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hao.common.CustomIdGenerator;
import com.hao.mapper.middle.TruckTypeGoodsTypeMapper;
import com.hao.pojo.middle.TruckTypeGoodsType;
import com.hao.service.middle.ITruckTypeGoodsTypeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class TruckTypeGoodsTypeServiceImpl extends ServiceImpl<TruckTypeGoodsTypeMapper, TruckTypeGoodsType> implements ITruckTypeGoodsTypeService {

    @Autowired
    private TruckTypeGoodsTypeMapper truckTypeGoodsTypeMapper;
    @Autowired
    private CustomIdGenerator idGenerator;

    @Override
    public boolean saveTruckTypeGoodsTypes(List<TruckTypeGoodsType> truckTypeGoodsTypes) {
        truckTypeGoodsTypes.forEach(truckTypeGoodsType ->{
            truckTypeGoodsType.setId(idGenerator.nextId(truckTypeGoodsType)+"");
        });
        return saveBatch(truckTypeGoodsTypes);
    }

    @Override
    public List<TruckTypeGoodsType> findByTypeId(String truckTypeId, String goodsTypeId) {
        LambdaQueryWrapper<TruckTypeGoodsType> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        if(truckTypeId != null){
            lambdaQueryWrapper.eq(TruckTypeGoodsType::getTruckTypeId,truckTypeId);
        }
        if(goodsTypeId != null){
            lambdaQueryWrapper.eq(TruckTypeGoodsType::getGoodsTypeId,goodsTypeId);
        }
        return truckTypeGoodsTypeMapper.selectList(lambdaQueryWrapper);
    }

    @Override
    public boolean deleteByTypeId(String truckTypeId, String goodsTypeId) {
        LambdaQueryWrapper<TruckTypeGoodsType> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        boolean canExecute = false;
        if(truckTypeId != null){
            lambdaQueryWrapper.eq(TruckTypeGoodsType::getTruckTypeId,truckTypeId);
            canExecute = true;
        }
        if(goodsTypeId != null){
            lambdaQueryWrapper.eq(TruckTypeGoodsType::getGoodsTypeId,goodsTypeId);
            canExecute = true;
        }
        if(canExecute){
            int delete = truckTypeGoodsTypeMapper.delete(lambdaQueryWrapper);
            if(delete == 1){
                return true;
            }
        }
        return false;
    }
}
