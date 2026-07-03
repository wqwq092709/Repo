package com.sky;

import com.sky.entity.Orders;
import com.sky.mapper.OrderMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Component
@Slf4j
public class MyTask {

    @Autowired
    private OrderMapper orderMapper;

    /**
     * 订单超时取消任务
     */
    @Scheduled(cron = "0 * * * * ?")
    public void processOrderOutTime(){
        log.info("查询订单是否超时任务开启...");
        //查询订单超过下单后15分且处于未支付状态的
        Orders orders = Orders.builder()
                .status(Orders.PENDING_PAYMENT).
                orderTime(LocalDateTime.now().plusMinutes(-15))
                .build();

        List<Orders> outTimeOrders = orderMapper.getOutTimeOrder(orders);

        //开始处理
        if(outTimeOrders != null && !outTimeOrders.isEmpty()){
            log.info("开始处理超时订单...");
            for (Orders order : outTimeOrders) {
                order.setCancelTime(LocalDateTime.now());
                order.setCancelReason("订单超时");
                order.setStatus(Orders.CANCELLED);
            }
            orderMapper.batchUpdate(outTimeOrders);
        }

    }

    /**
     * 处理订单一直处于派送状态
     */
    @Scheduled(cron = "0/5 * * * * ?")
    public void processOrderIsDELIVERY(){
        log.info("处理长时间处于派送状态的订单任务开启");
        //查询下单时间超过一个小时且处于派送中的订单
        Orders orders = Orders.builder()
                .status(Orders.DELIVERY_IN_PROGRESS)
                .orderTime(LocalDateTime.now().plusMinutes(-60))
                .build();
        List<Orders> orderIsDELIVERYs = orderMapper.getOrderIsDELIVERY(orders);

        //处理一直处于派送的订单
        if(orderIsDELIVERYs != null && !orderIsDELIVERYs.isEmpty()){
            log.info("发现长时间处于派送的订单,开始处理...");
            orderIsDELIVERYs.forEach(order ->{
                //更新为已完成
                order.setStatus(Orders.COMPLETED);
            });
            orderMapper.batchUpdate(orderIsDELIVERYs);
        }
    }

}
