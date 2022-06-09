package com.hao.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.hao.DTO.OrderDTO;
import com.hao.DTO.OrderLocationDto;
import com.hao.DTO.OrderSearchDTO;
import com.hao.common.CustomIdGenerator;
import com.hao.pojo.Order;
import com.hao.pojo.OrderLocation;
import com.hao.service.IOrderLocationService;
import com.hao.service.IOrderService;
import com.hao.utils.PageResponse;
import io.swagger.annotations.Api;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Log4j2
@RestController
@RequestMapping("/order")
@Api(tags = "订单管理")
public class OrderController {
    @Autowired
    private IOrderService orderService;
    @Autowired
    private IOrderLocationService orderLocationService;
    @Autowired
    private CustomIdGenerator idGenerator;

    /**
     * 新增订单
     * @param orderDTO
     * @return
     */
    @PostMapping("")
    public OrderDTO save(@RequestBody OrderDTO orderDTO){
        Order order = new Order();
        Map map = orderService.calculateAmount(orderDTO);
        orderDTO = (OrderDTO) map.get("orderDTO");
        BeanUtils.copyProperties(orderDTO,order);
        if("sender error msg".equals(orderDTO.getSenderAddress()) || "receiver error msg".equals(orderDTO.getReceiverAddress())){
            return orderDTO;
        }
        order = orderService.saveOrder(order);
        BeanUtils.copyProperties(order,orderDTO);
        return orderDTO;
    }

    /**
     * 修改订单
     * @param id
     * @param orderDTO
     * @return
     */
    @PutMapping("/{id}")
    public OrderDTO update(@PathVariable("id")String id,@RequestBody OrderDTO orderDTO){
        orderDTO.setId(id);
        Order order = new Order();
        BeanUtils.copyProperties(orderDTO,order);
        boolean update = orderService.updateById(order);
        if(update){
            return orderDTO;
        }
        return null;
    }

    /**
     * 获取订单分页数据
     *
     * @param orderDTO 查询条件
     * @return 订单分页数据
     */
    @GetMapping("/page")
    public PageResponse<OrderDTO> findByPage(@RequestBody OrderDTO orderDTO){
        Order queryOrder = new Order();
        BeanUtils.copyProperties(orderDTO,queryOrder);
        IPage<Order> iPage = orderService.fingByPage(orderDTO.getPage(), orderDTO.getPageSize(), queryOrder);
        List<Order> orders = iPage.getRecords();
        if(orders != null){
            List<OrderDTO> orderDTOS = orders.stream().map(order -> {
                OrderDTO dto = new OrderDTO();
                BeanUtils.copyProperties(order, dto);
                return dto;
            }).collect(Collectors.toList());
            return PageResponse.<OrderDTO>builder().items(orderDTOS).page(orderDTO.getPage()).pagesize(orderDTO.getPageSize())
                    .pages(iPage.getPages()).counts(iPage.getTotal()).build();
        }
        return null;
    }

    /**
     * 根据id获取订单详情
     *
     * @param id 订单Id
     * @return 订单详情
     */
    @GetMapping("/{id}")
    public OrderDTO findById(@PathVariable("id")String id){
        Order order = orderService.getById(id);
        if(order != null){
            OrderDTO orderDTO = new OrderDTO();
            BeanUtils.copyProperties(order,orderDTO);
            return orderDTO;
        }
        return null;
    }

    /**
     * 根据id获取集合
     *
     * @param ids 订单Id
     * @return 订单详情
     */
    @GetMapping("/ids")
    public List<OrderDTO> fingByIds(@RequestParam("ids")List<String> ids){
        List<Order> orders = orderService.findByIds(ids);
        if(orders != null && orders.size() > 0){
            return orders.stream().map(order -> {
                OrderDTO orderDTO = new OrderDTO();
                BeanUtils.copyProperties(order, orderDTO);
                return orderDTO;
            }).collect(Collectors.toList());
        }
        return null;
    }

    /**
     * 获取订单分页数据 客户端使用
     *
     * @return 订单分页数据
     */
    @PostMapping("pageLikeForCustomer")
    public PageResponse<OrderDTO> pageLikeForCustomer(@RequestBody OrderSearchDTO orderSearchDTO) {

        //查询结果
        IPage<Order> orderIPage = orderService.pageLikeForCustomer(orderSearchDTO);
        if(orderIPage.getRecords() != null){
            List<OrderDTO> dtoList = new ArrayList<>();
            orderIPage.getRecords().forEach(order -> {
                OrderDTO dto = new OrderDTO();
                BeanUtils.copyProperties(order, dto);
                dtoList.add(dto);
            });
            return PageResponse.<OrderDTO>builder().items(dtoList).pagesize(orderSearchDTO.getPageSize())
                    .page(orderSearchDTO.getPage()).counts(orderIPage.getTotal()).pages(orderIPage.getPages()).build();
        }
        return null;
    }
    @ResponseBody
    @PostMapping("list")
    public List<Order> list(@RequestBody OrderSearchDTO orderSearchDTO) {
        LambdaQueryWrapper<Order> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(orderSearchDTO.getStatus() != null, Order::getStatus, orderSearchDTO.getStatus());
        wrapper.in(!CollectionUtils.isEmpty(orderSearchDTO.getReceiverCountyIds()), Order::getReceiverCountyId, orderSearchDTO.getReceiverCountyIds());
        wrapper.in(!CollectionUtils.isEmpty(orderSearchDTO.getSenderCountyIds()), Order::getSenderCountyId, orderSearchDTO.getSenderCountyIds());
        wrapper.eq(StringUtils.isNotEmpty(orderSearchDTO.getCurrentAgencyId()), Order::getCurrentAgencyId, orderSearchDTO.getCurrentAgencyId());

        return orderService.list(wrapper);
    }


    @ResponseBody
    @PostMapping("location/saveOrUpdate")
    public OrderLocationDto saveOrUpdateLoccation(@RequestBody OrderLocationDto orderLocationDto) {
        String id = orderLocationDto.getId();
        String orderId = orderLocationDto.getOrderId();
        if (StringUtils.isNotBlank(id)) {
            OrderLocation location = orderLocationService.getBaseMapper().selectOne(new QueryWrapper<OrderLocation>().eq("order_id", orderId).last(" limit 1"));
            if (location != null) {
                OrderLocation orderLocationUpdate = new OrderLocation();
                BeanUtils.copyProperties(orderLocationDto, orderLocationUpdate);
                orderLocationUpdate.setId(location.getId());
                orderLocationService.getBaseMapper().updateById(orderLocationUpdate);
            }
        } else {
            OrderLocation orderLocation = new OrderLocation();
            BeanUtils.copyProperties(orderLocationDto, orderLocation);
            orderLocation.setId(idGenerator.nextId(orderLocation).toString());
            orderLocationService.save(orderLocation);
            BeanUtils.copyProperties(orderLocation, orderLocationDto);
        }

        return orderLocationDto;
    }

    @GetMapping("orderId")
    public OrderLocationDto selectByOrderId(@RequestParam(name = "orderId") String orderId) {
        OrderLocationDto result = new OrderLocationDto();
        OrderLocation location = orderLocationService.getBaseMapper().selectOne(new QueryWrapper<OrderLocation>().eq("order_id", orderId).last(" limit 1"));
        BeanUtils.copyProperties(location, result);
        return result;
    }

    @PostMapping("del")
    public int deleteOrderLocation(@RequestBody OrderLocationDto orderLocationDto) {
        String orderId = orderLocationDto.getOrderId();
        int result = 0;
        if (StringUtils.isNotBlank(orderId)) {
            result = orderLocationService.getBaseMapper().delete(new UpdateWrapper<OrderLocation>().eq("order_id", orderLocationDto));
        }
        return result;
    }
}
