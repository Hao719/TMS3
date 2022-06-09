package com.hao.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.hao.pojo.TaskPickupDispatch;

import java.util.List;

/**
 * 取件、派件任务服务接口
 */
public interface PickupDispatchTaskService extends IService<TaskPickupDispatch> {

    /**
     * 新增取派件任务
     *
     * @param taskPickupDispatch 取派件任务信息
     * @return 取派件任务信息
     */
    TaskPickupDispatch saveTaskPickupDispatch(TaskPickupDispatch taskPickupDispatch);

    /**
     * 获取取派件任务分页数据
     *
     * @param page     页码
     * @param pageSize 页尺寸
     * @param taskPickupDispatch 查询条件
     * @return 取派件任务分页数据
     */
    IPage<TaskPickupDispatch> findByPage(Integer page,Integer pageSize,TaskPickupDispatch taskPickupDispatch);

    /**
     * 获取取派件任务列表
     *
     * @param ids      取派件任务id列表
     * @param dispatch 查询条件
     * @return 取派件任务列表
     */
    List<TaskPickupDispatch> findAll(List<String> ids, List<String> orderIds, TaskPickupDispatch dispatch);
}
