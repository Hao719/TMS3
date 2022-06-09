package com.hao.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.hao.pojo.TransportOrder;
import org.apache.ibatis.annotations.Mapper;

/**
 * 运单Mapper接口
 */
@Mapper
public interface TransportOrderMapper extends BaseMapper<TransportOrder> {
}
