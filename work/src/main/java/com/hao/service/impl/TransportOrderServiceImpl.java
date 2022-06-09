package com.hao.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hao.common.CustomIdGenerator;
import com.hao.enums.transportorder.TransportOrderSchedulingStatus;
import com.hao.enums.transportorder.TransportOrderStatus;
import com.hao.mapper.TransportOrderMapper;
import com.hao.pojo.TransportOrder;
import com.hao.service.ITransportOrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

/**
 * 运单服务实现类
 */
@Service
public class TransportOrderServiceImpl extends ServiceImpl<TransportOrderMapper, TransportOrder> implements ITransportOrderService {
    @Autowired
    private CustomIdGenerator idGenerator;

    /**
     * 新增运单
     * @param transportOrder 运单信息
     * @return
     */
    @Override
    public TransportOrder saveTransportOrder(TransportOrder transportOrder) {
        transportOrder.setCreateTime(LocalDateTime.now());
        transportOrder.setId(idGenerator.nextId(transportOrder) + "");
        transportOrder.setStatus(TransportOrderStatus.CREATED.getCode());
        transportOrder.setSchedulingStatus(TransportOrderSchedulingStatus.TO_BE_SCHEDULED.getCode());
        boolean save = save(transportOrder);
        if (save)
            return transportOrder;
        return null;
    }

    /**
     * 通过订单id获取运单信息
     * @param page             页码
     * @param pageSize         页尺寸
     * @param orderId          订单Id
     * @param status           运单状态
     * @param schedulingStatus 运单调度状态
     * @return
     */
    @Override
    public IPage<TransportOrder> findByPage(Integer page, Integer pageSize, String orderId, Integer status, Integer schedulingStatus) {
        Page<TransportOrder> iPage = new Page(page, pageSize);
        LambdaQueryWrapper<TransportOrder> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        if (StringUtils.isNotEmpty(orderId)) {
            lambdaQueryWrapper.like(TransportOrder::getOrderId, orderId);
        }
        if (status != null) {
            lambdaQueryWrapper.eq(TransportOrder::getStatus, status);
        }
        if (schedulingStatus != null) {
            lambdaQueryWrapper.eq(TransportOrder::getSchedulingStatus, schedulingStatus);
        }
        return page(iPage, lambdaQueryWrapper);
    }

    /**
     * 通过订单id获取运单信息
     * @param orderId 订单id
     * @return
     */
    @Override
    public TransportOrder findByOrderId(String orderId) {
        return getOne(new LambdaQueryWrapper<TransportOrder>().eq(TransportOrder::getOrderId, orderId));
    }
}
