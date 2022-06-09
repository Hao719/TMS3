package com.hao.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.hao.DTO.TaskPickupDispatchDTO;
import com.hao.enums.pickuptask.PickupDispatchTaskAssignedStatus;
import com.hao.pojo.TaskPickupDispatch;
import com.hao.service.PickupDispatchTaskService;
import com.hao.utils.PageResponse;
import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RestController
@Api(tags = "取派件管理")
@RequestMapping("pickup-dispatch-task")
public class PickupDispatchTaskController {
    @Autowired
    private PickupDispatchTaskService pickupDispatchTaskService;

    /**
     * 新增取派件任务
     *
     * @param taskPickupDispatchDTO 取派件任务信息
     * @return 取派件任务信息
     */
    @PostMapping()
    public TaskPickupDispatchDTO save(@RequestBody TaskPickupDispatchDTO taskPickupDispatchDTO){
        TaskPickupDispatch taskPickupDispatch = new TaskPickupDispatch();
        BeanUtils.copyProperties(taskPickupDispatchDTO,taskPickupDispatch);
        taskPickupDispatch = pickupDispatchTaskService.saveTaskPickupDispatch(taskPickupDispatch);
        BeanUtils.copyProperties(taskPickupDispatch,taskPickupDispatchDTO);
        return taskPickupDispatchDTO;
    }

    /**
     * 修改取派件任务信息
     *
     * @param id  取派件任务id
     * @param taskPickupDispatchDTO 取派件任务信息
     * @return 取派件任务信息
     */
    @PutMapping("/{id}")
    public TaskPickupDispatchDTO update(@PathVariable(name = "id")String id,@RequestBody TaskPickupDispatchDTO taskPickupDispatchDTO){
        taskPickupDispatchDTO.setId(id);
        if (StringUtils.isNotEmpty(taskPickupDispatchDTO.getCourierId())) {
            taskPickupDispatchDTO.setAssignedStatus(PickupDispatchTaskAssignedStatus.DISTRIBUTED.getCode());
        }
        TaskPickupDispatch taskPickupDispatch = new TaskPickupDispatch();
        BeanUtils.copyProperties(taskPickupDispatchDTO,taskPickupDispatch);
        pickupDispatchTaskService.updateById(taskPickupDispatch);
        return taskPickupDispatchDTO;
    }

    /**
     * 获取取派件任务分页数据
     *
     * @param taskPickupDispatchDTO 查询条件
     * @return 取派件分页数据
     */
    @GetMapping("/page")
    public PageResponse<TaskPickupDispatchDTO> findByPage(@RequestBody TaskPickupDispatchDTO taskPickupDispatchDTO){
        TaskPickupDispatch taskPickupDispatch = new TaskPickupDispatch();
        BeanUtils.copyProperties(taskPickupDispatchDTO,taskPickupDispatch);
        if (taskPickupDispatchDTO.getPage() == null) {
            taskPickupDispatchDTO.setPage(1);
        }
        if (taskPickupDispatchDTO.getPageSize() == null) {
            taskPickupDispatchDTO.setPageSize(10);
        }
        IPage<TaskPickupDispatch> iPage = pickupDispatchTaskService.findByPage(taskPickupDispatchDTO.getPage(), taskPickupDispatchDTO.getPageSize(), taskPickupDispatch);
        List<TaskPickupDispatchDTO> dispatchDTOS = iPage.getRecords().stream().map(dispath -> {
            TaskPickupDispatchDTO dispatchDTO = new TaskPickupDispatchDTO();
            BeanUtils.copyProperties(dispath, dispatchDTO);
            return dispatchDTO;
        }).collect(Collectors.toList());
        return PageResponse.<TaskPickupDispatchDTO>builder()
                .items(dispatchDTOS)
                .page(taskPickupDispatchDTO.getPage())
                .pagesize(taskPickupDispatchDTO.getPageSize())
                .pages(iPage.getPages())
                .counts(iPage.getTotal())
                .build();
    }

    /**
     * 获取取派件任务列表
     *
     * @param dto 查询条件
     * @return 取派件任务列表
     */
    @PostMapping("/list")
    public List<TaskPickupDispatchDTO> findAll(@RequestBody TaskPickupDispatchDTO dto) {
        TaskPickupDispatch queryTask = new TaskPickupDispatch();
        BeanUtils.copyProperties(dto, queryTask);
        return pickupDispatchTaskService.findAll(dto.getIds(), dto.getOrderIds(), queryTask).stream().map(taskPickupDispatch -> {
            TaskPickupDispatchDTO resultDto = new TaskPickupDispatchDTO();
            BeanUtils.copyProperties(taskPickupDispatch, resultDto);
            return resultDto;
        }).collect(Collectors.toList());
    }

    /**
     * 根据id获取取派件任务信息
     *
     * @param id 任务Id
     * @return 任务详情
     */
    @GetMapping("/{id}")
    public TaskPickupDispatchDTO findById(@PathVariable(name = "id") String id) {
        TaskPickupDispatchDTO dto = new TaskPickupDispatchDTO();
        TaskPickupDispatch dispatch = pickupDispatchTaskService.getById(id);
        if (dispatch != null) {
            BeanUtils.copyProperties(dispatch, dto);
        } else {
            dto = null;
        }
        return dto;
    }

    /**
     * 根据订单id获取取派件任务信息
     *
     * @param orderId 订单Id
     * @return 任务详情
     */
    @GetMapping("/orderId/{orderId}/{taskType}")
    public TaskPickupDispatchDTO findByOrderId(@PathVariable("orderId") String orderId, @PathVariable("taskType") Integer taskType) {
        TaskPickupDispatchDTO dto = new TaskPickupDispatchDTO();
        LambdaQueryWrapper<TaskPickupDispatch> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(TaskPickupDispatch::getOrderId, orderId);
        wrapper.eq(TaskPickupDispatch::getTaskType, taskType);
        TaskPickupDispatch dispatch = pickupDispatchTaskService.getOne(wrapper);
        if (dispatch != null) {
            BeanUtils.copyProperties(dispatch, dto);
        } else {
            dto = null;
        }
        return dto;
    }
}
