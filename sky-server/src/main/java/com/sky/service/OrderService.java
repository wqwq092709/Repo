package com.sky.service;

import com.sky.dto.OrdersDTO;
import com.sky.vo.OrderSubmitVO;

public interface OrderService {
    /**
     * 提交订单
     * @param ordersDTO
     * @return
     */
    OrderSubmitVO submit(OrdersDTO ordersDTO);
}
