package com.hao.mapper.base;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.hao.pojo.base.GoodsType;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface GoodsTypeMapper extends BaseMapper<GoodsType> {

    List<GoodsType> findByPage(Page<GoodsType> page,
                               @Param("name") String name,
                               @Param("truckTypeId") String truckTypeId,
                               @Param("truckTypeName") String truckTypeName);
}
