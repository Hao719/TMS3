package com.hao.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.hao.DTO.TransportOrderDTO;
import com.hao.DTO.TransportOrderSearchDTO;
import com.hao.pojo.TransportOrder;
import com.hao.service.ITransportOrderService;
import com.hao.utils.PageResponse;
import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import net.oschina.j2cache.CacheChannel;
import net.oschina.j2cache.CacheObject;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 运单管理
 */
@Api(tags = "运单管理")
@Slf4j
@RestController
@RequestMapping("transport-order")
public class TransportOrderController {
    @Autowired
    private ITransportOrderService transportOrderService;
    @Autowired
    private CacheChannel cacheChannel;
    private final String region="transport-order";

    /**
     * 新增运单
     *
     * @param dto 运单信息
     * @return 运单信息
     */
    @PostMapping("")
    public TransportOrderDTO save(@RequestBody TransportOrderDTO dto) {
        TransportOrder transportOrder = new TransportOrder();
        BeanUtils.copyProperties(dto, transportOrder);
        transportOrder = transportOrderService.saveTransportOrder(transportOrder);
        if (transportOrder != null){
            BeanUtils.copyProperties(transportOrder, dto);
            cacheChannel.set("transport-order",transportOrder.getId(),transportOrder);
            return dto;
        }
        return null;
    }

    /**
     * 修改运单信息
     *
     * @param id  运单id
     * @param dto 运单信息
     * @return 运单信息
     */
    @PutMapping("/{id}")
    public TransportOrderDTO updateById(@PathVariable(name = "id") String id, @RequestBody TransportOrderDTO dto) {
        dto.setId(id);
        TransportOrder transportOrder = new TransportOrder();
        BeanUtils.copyProperties(dto, transportOrder);
        boolean update = transportOrderService.updateById(transportOrder);
        if (update){
            cacheChannel.evict("transport-order",id);
            return dto;
        }
        return null;
    }

    /**
     * 获取运单分页数据
     *
     * @param page             页码
     * @param pageSize         页尺寸
     * @param orderId          订单ID
     * @param status           运单状态(1.新建 2.已装车，发往x转运中心 3.到达 4.到达终端网点)
     * @param schedulingStatus 调度状态调度状态(1.待调度2.未匹配线路3.已调度)
     * @return 运单分页数据
     */
    @GetMapping("/page")
    public PageResponse<TransportOrderDTO> findByPage(@RequestParam(name = "page") Integer page,
                                                      @RequestParam(name = "pageSize") Integer pageSize,
                                                      @RequestParam(name = "orderId", required = false) String orderId,
                                                      @RequestParam(name = "status", required = false) Integer status,
                                                      @RequestParam(name = "schedulingStatus", required = false) Integer schedulingStatus) {
        IPage<TransportOrder> orderIPage = transportOrderService.findByPage(page, pageSize, orderId, status, schedulingStatus);
        List<TransportOrderDTO> dtoList = new ArrayList<>();
        orderIPage.getRecords().forEach(order -> {
            TransportOrderDTO resultDto = new TransportOrderDTO();
            BeanUtils.copyProperties(order, resultDto);
            dtoList.add(resultDto);
        });
        return PageResponse.<TransportOrderDTO>builder().items(dtoList).pagesize(pageSize).page(page).counts(orderIPage.getTotal())
                .pages(orderIPage.getPages()).build();
    }

    /**
     * 根据id获取运单信息
     *
     * @param id 运单id
     * @return 运单信息
     */
    @GetMapping("/{id}")
    public TransportOrderDTO findById(@PathVariable(name = "id") String id) {
        TransportOrderDTO dto = new TransportOrderDTO();
        TransportOrder transportOrder = null;
        boolean exists = cacheChannel.exists(region, id);
        if (exists){
            CacheObject cacheObject = cacheChannel.get(region, id);
            transportOrder = (TransportOrder) cacheObject.getValue();
        }else {
            System.out.println("123123");
            transportOrder = transportOrderService.getById(id);
        }
        if (transportOrder != null) {
            BeanUtils.copyProperties(transportOrder, dto);
        } else {
            dto = null;
        }
        return dto;
    }

    /**
     * 根据订单id获取运单信息
     *
     * @param orderId 订单id
     * @return 运单信息
     */
    @GetMapping("/orderId/{orderId}")
    public TransportOrderDTO findByOrderId(@PathVariable(name = "orderId") String orderId) {
        TransportOrderDTO dto = new TransportOrderDTO();
        TransportOrder transportOrder = transportOrderService.findByOrderId(orderId);
        if (transportOrder != null) {
            BeanUtils.copyProperties(transportOrder, dto);
        } else {
            dto = null;
        }
        return dto;
    }

    /**
     * 根据多个订单id查询运单信息
     *
     * @param ids
     * @return
     */
    @GetMapping("orderIds")
    public List<TransportOrderDTO> findByOrderIds(@RequestParam(name = "ids") List<String> ids) {
        LambdaQueryWrapper<TransportOrder> wrapper = new LambdaQueryWrapper();
        wrapper.in(TransportOrder::getOrderId, ids);
        List<TransportOrder> transportOrders = transportOrderService.list(wrapper);
        return transportOrders.stream().map(item -> {
            TransportOrderDTO dto = new TransportOrderDTO();
            BeanUtils.copyProperties(item, dto);
            return dto;
        }).collect(Collectors.toList());
    }

    /**
     * 根据多个参数获取运单信息
     *
     * @param transportOrderSearchDTO
     * @return
     */
    @PostMapping("list")
    public List<TransportOrderDTO> list(@RequestBody TransportOrderSearchDTO transportOrderSearchDTO) {
        LambdaQueryWrapper<TransportOrder> wrapper = new LambdaQueryWrapper();
        wrapper.eq(transportOrderSearchDTO.getStatus() != null, TransportOrder::getStatus, transportOrderSearchDTO.getStatus());
        wrapper.eq(transportOrderSearchDTO.getSchedulingStatus() != null, TransportOrder::getSchedulingStatus, transportOrderSearchDTO.getSchedulingStatus());
        wrapper.in(!CollectionUtils.isEmpty(transportOrderSearchDTO.getOrderIds()), TransportOrder::getOrderId, transportOrderSearchDTO.getOrderIds());
        List<TransportOrder> transportOrders = transportOrderService.list(wrapper);
        return transportOrders.stream().map(item -> {
            TransportOrderDTO dto = new TransportOrderDTO();
            BeanUtils.copyProperties(item, dto);
            return dto;
        }).collect(Collectors.toList());
    }
}
