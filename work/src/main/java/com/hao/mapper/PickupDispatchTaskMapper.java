package com.hao.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.hao.pojo.TaskPickupDispatch;
import org.apache.ibatis.annotations.Mapper;

/**
 * 取件、派件任务Mapper接口
 */
@Mapper
public interface PickupDispatchTaskMapper extends BaseMapper<TaskPickupDispatch> {

}
