package com.hao.controller.truck;

import com.hao.DTO.truck.TruckTypeDto;
import io.swagger.annotations.Api;
import lombok.extern.log4j.Log4j2;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Log4j2
@RestController
@RequestMapping("/truckType")
@Api(tags = "车辆类型管理")
public class TruckTypeController {



    @PostMapping("")
    public TruckTypeDto save(){

        return null;
    }
}
