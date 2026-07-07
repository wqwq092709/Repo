package com.sky.service;

import com.sky.dto.OrdersPageQueryDTO;
import com.sky.dto.OrdersPaymentDTO;
import com.sky.dto.OrdersSubmitDTO;
import com.sky.entity.Orders;
import com.sky.result.PageResult;
import com.sky.vo.OrderPaymentVO;
import com.sky.vo.OrderSubmitVO;
import com.sky.vo.OrderVO;

public interface OrderService {
    /**
     * 提交订单
     */
    OrderSubmitVO submit(OrdersSubmitDTO ordersSubmitDTO);

    /**
     * 订单支付
     */
    OrderPaymentVO payment(OrdersPaymentDTO ordersPaymentDTO) throws Exception;

    /**
     * 支付成功，修改订单状态
     */
    void paySuccess(String outTradeNo);

    /**
     * 历史订单查询
     */
    PageResult historyOrders(OrdersPageQueryDTO ordersPageQueryDTO);

    /**
     * 根据订单id查询订单
     */
    Orders getByOrderId(Integer orderId);

    /**
     * 查询订单详情（含订单菜品字符串）
     */
    OrderVO getOrderDetail(Long orderId);

    /**
     * 接单
     */
    void confirmOrder(Long orderId);

    /**
     * 拒单
     */
    void rejectOrder(Long orderId, String rejectionReason);

    /**
     * 取消订单
     */
    void cancelOrder(Long orderId, String cancelReason);

    /**
     * 派送订单
     */
    void deliveryOrder(Long orderId);

    /**
     * 完成订单
     */
    void completeOrder(Long orderId);
}
