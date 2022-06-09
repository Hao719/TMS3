package com.hao.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hao.common.CustomIdGenerator;
import com.hao.enums.pickuptask.PickupDispatchTaskAssignedStatus;
import com.hao.enums.pickuptask.PickupDispatchTaskStatus;
import com.hao.mapper.PickupDispatchTaskMapper;
import com.hao.pojo.TaskPickupDispatch;
import com.hao.service.PickupDispatchTaskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 取件、派件任务服务实现类
 */
@Service
public class PickupDispatchTaskServiceImpl extends ServiceImpl<PickupDispatchTaskMapper, TaskPickupDispatch> implements PickupDispatchTaskService {
    @Autowired
    private PickupDispatchTaskMapper pickupDispatchTaskMapper;
    @Autowired
    private CustomIdGenerator idGenerator;

    /**
     * 新增取派件任务
     *
     * @param taskPickupDispatch 取派件任务信息
     * @return 取派件任务信息
     */
    @Override
    public TaskPickupDispatch saveTaskPickupDispatch(TaskPickupDispatch taskPickupDispatch) {
        taskPickupDispatch.setId(idGenerator.nextId(taskPickupDispatch)+"");
        taskPickupDispatch.setStatus(PickupDispatchTaskStatus.PENDING.getCode());
        taskPickupDispatch.setCreateTime(LocalDateTime.now());
        taskPickupDispatch.setAssignedStatus(PickupDispatchTaskAssignedStatus.TO_BE_DISTRIBUTED.getCode());
        save(taskPickupDispatch);
        return taskPickupDispatch;
    }

    /**
     * 获取取派件任务分页数据
     * @param page     页码
     * @param pageSize 页尺寸
     * @param taskPickupDispatch 查询条件
     * @return
     */
    @Override
    public IPage<TaskPickupDispatch> findByPage(Integer page, Integer pageSize, TaskPickupDispatch taskPickupDispatch) {
        Page<TaskPickupDispatch> dispatchPage = new Page<>(page, pageSize);
        LambdaQueryWrapper<TaskPickupDispatch> queryWrapper = new LambdaQueryWrapper<>();
        if (StringUtils.isNotBlank(taskPickupDispatch.getCourierId())) {
            queryWrapper.eq(TaskPickupDispatch::getCourierId, taskPickupDispatch.getCourierId());
        }
        if (taskPickupDispatch.getAssignedStatus() != null) {
            queryWrapper.eq(TaskPickupDispatch::getAssignedStatus, taskPickupDispatch.getAssignedStatus());
        }
        if (taskPickupDispatch.getTaskType() != null) {
            queryWrapper.eq(TaskPickupDispatch::getTaskType, taskPickupDispatch.getTaskType());
        }
        if (taskPickupDispatch.getStatus() != null) {
            queryWrapper.eq(TaskPickupDispatch::getStatus, taskPickupDispatch.getStatus());
        }
        queryWrapper.orderBy(true, false, TaskPickupDispatch::getId);
        return page(dispatchPage, queryWrapper);
    }

    /**
     * 获取取派件任务列表
     * @param ids      取派件任务id列表
     * @param orderIds
     * @param dispatch 查询条件
     * @return
     */
    @Override
    public List<TaskPickupDispatch> findAll(List<String> ids, List<String> orderIds, TaskPickupDispatch dispatch) {
        LambdaQueryWrapper<TaskPickupDispatch> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        if (ids != null && ids.size() > 0) {
            lambdaQueryWrapper.in(TaskPickupDispatch::getId, ids);
        }
        if (orderIds != null && orderIds.size() > 0) {
            lambdaQueryWrapper.in(TaskPickupDispatch::getOrderId, orderIds);
        }
        if (dispatch.getAssignedStatus() != null) {
            lambdaQueryWrapper.eq(TaskPickupDispatch::getAssignedStatus, dispatch.getAssignedStatus());
        }
        if (dispatch.getTaskType() != null) {
            lambdaQueryWrapper.eq(TaskPickupDispatch::getTaskType, dispatch.getTaskType());
        }
        if (dispatch.getStatus() != null) {
            lambdaQueryWrapper.eq(TaskPickupDispatch::getStatus, dispatch.getStatus());
        }
        if (StringUtils.isNotEmpty(dispatch.getOrderId())) {
            lambdaQueryWrapper.like(TaskPickupDispatch::getOrderId, dispatch.getOrderId());
        }
        lambdaQueryWrapper.orderBy(true, false, TaskPickupDispatch::getId);
        return list(lambdaQueryWrapper);
    }
}
