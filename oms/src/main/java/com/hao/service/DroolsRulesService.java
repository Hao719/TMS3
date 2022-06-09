package com.hao.service;

import com.hao.pojo.fact.AddressRule;

public interface DroolsRulesService {
    /**
     * 根据条件计算订单价格
     * @param addressRule
     * @return
     */
    String calcFee(AddressRule addressRule);
}
