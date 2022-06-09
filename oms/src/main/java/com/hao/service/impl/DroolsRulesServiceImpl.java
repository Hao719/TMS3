package com.hao.service.impl;

import com.hao.pojo.fact.AddressRule;
import com.hao.service.DroolsRulesService;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class DroolsRulesServiceImpl implements DroolsRulesService {

    @Override
    public String calcFee(AddressRule addressRule) {
        //货物总重量
        BigDecimal totalWeight = BigDecimal.valueOf(addressRule.getTotalWeight());
        //首重
        BigDecimal firstWeight = BigDecimal.valueOf(addressRule.getFirstWeight());
        //首重价格
        BigDecimal firstFee = BigDecimal.valueOf(addressRule.getFirstFee());
        //续重价格
        BigDecimal continuedFee = BigDecimal.valueOf(addressRule.getContinuedFee());
        //超过首重部分重量=总重量-首重
        BigDecimal lost = totalWeight.subtract(firstWeight);//3.5
        //只保留整数部分
        lost = lost.setScale(0, RoundingMode.DOWN);
        return continuedFee.multiply(lost).add(firstFee).toString();
    }
}
