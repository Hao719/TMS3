package com.hao.service.middle;

import com.baomidou.mybatisplus.extension.service.IService;
import com.hao.pojo.middle.TruckTypeGoodsType;

import java.util.List;

public interface ITruckTypeGoodsTypeService extends IService<TruckTypeGoodsType> {

    boolean saveTruckTypeGoodsTypes(List<TruckTypeGoodsType> truckTypeGoodsTypes);

    List<TruckTypeGoodsType> findByTypeId(String truckTypeId,String goodsTypeId);

    boolean deleteByTypeId(String truckTypeId,String goodsTypeId);
}
