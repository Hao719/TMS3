package com.hao.service.base;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.hao.pojo.base.GoodsType;

import java.util.List;

public interface IGoodsTypeService extends IService<GoodsType>{

   GoodsType findById(String id);

   List<GoodsType> findAll();

   IPage<GoodsType> findByPage(Integer page,Integer pageSize,String name,String truckTypeId,String truckTypeName);

}
