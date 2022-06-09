package com.hao.controller;

import com.hao.pojo.Rule;
import com.hao.service.impl.ReloadDroolsRulesService;
import com.hao.utils.Result;
import io.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@RequestMapping("/rules")
@Controller
@Api(tags = "规则管理")
public class RulesReloadController {
    @Autowired
    private ReloadDroolsRulesService reloadDroolsRulesService;


    /**
     * 从数据库加载最新规则
     *
     * @return
     * @throws IOException
     */
    @ResponseBody
    @GetMapping ("")
    public Result reload() throws IOException {
        reloadDroolsRulesService.reload();
        return Result.ok();
    }

    /**
     * 新增规则
     * @param rule
     * @return
     */
    @PostMapping("")
    public Result save(@Validated @RequestBody Rule rule){

        boolean save = reloadDroolsRulesService.saveRule(rule);
        if(save){
            return Result.ok();
        }
        return Result.error();
    }
}
