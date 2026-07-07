package com.sky.service.impl;

import com.sky.constant.StatusConstant;
import com.sky.entity.Orders;
import com.sky.mapper.DishMapper;
import com.sky.mapper.ReportMapper;
import com.sky.mapper.SetmealMapper;
import com.sky.service.WorkspaceService;
import com.sky.vo.BusinessDataVO;
import com.sky.vo.DishOverViewVO;
import com.sky.vo.OrderOverViewVO;
import com.sky.vo.SetmealOverViewVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.HashMap;
import java.util.Map;

@Service
@Slf4j
public class WorkspaceServiceImpl implements WorkspaceService {

    @Autowired
    private ReportMapper reportMapper;
    @Autowired
    private DishMapper dishMapper;
    @Autowired
    private SetmealMapper setmealMapper;

    public BusinessDataVO getBusinessData(LocalDateTime begin, LocalDateTime end) {
        Map<String, Object> map = new HashMap<>();
        map.put("begin", begin);
        map.put("end", end);

        // 总订单数
        Integer totalOrderCount = reportMapper.getOrderStatistics(map);
        totalOrderCount = totalOrderCount == null ? 0 : totalOrderCount;

        // 已完成订单：营业额 + 有效订单数
        map.put("status", Orders.COMPLETED);
        Double turnover = reportMapper.getTurnoverStatisticsByMap(map);
        turnover = turnover == null ? 0.0 : turnover;

        Integer validOrderCount = reportMapper.validOrderDayCount(map);
        validOrderCount = validOrderCount == null ? 0 : validOrderCount;

        // 订单完成率 & 客单价
        double orderCompletionRate = 0.0;
        double unitPrice = 0.0;
        if (totalOrderCount > 0 && validOrderCount > 0) {
            orderCompletionRate = (double) validOrderCount / totalOrderCount;
            unitPrice = turnover / validOrderCount;
        }

        // 新增用户
        map.remove("status");
        Integer newUsers = reportMapper.getUserStatisticsIncrementByMap(map);
        newUsers = newUsers == null ? 0 : newUsers;

        return BusinessDataVO.builder()
                .turnover(turnover)
                .validOrderCount(validOrderCount)
                .orderCompletionRate(orderCompletionRate)
                .unitPrice(unitPrice)
                .newUsers(newUsers)
                .build();
    }

    public OrderOverViewVO getOrderOverView() {
        Map<String, Object> map = new HashMap<>();
        map.put("begin", LocalDateTime.now().with(LocalTime.MIN));

        map.put("status", Orders.TO_BE_CONFIRMED);
        Integer waitingOrders = reportMapper.getOrderStatistics(map);

        map.put("status", Orders.CONFIRMED);
        Integer deliveredOrders = reportMapper.getOrderStatistics(map);

        map.put("status", Orders.DELIVERY_IN_PROGRESS);
        Integer deliveryInProgress = reportMapper.getOrderStatistics(map);

        map.put("status", Orders.COMPLETED);
        Integer completedOrders = reportMapper.getOrderStatistics(map);

        map.put("status", Orders.CANCELLED);
        Integer cancelledOrders = reportMapper.getOrderStatistics(map);

        map.remove("status");
        Integer allOrders = reportMapper.getOrderStatistics(map);

        OrderOverViewVO overViewVO = OrderOverViewVO.builder()
                .waitingOrders(waitingOrders == null ? 0 : waitingOrders)
                .deliveredOrders(deliveredOrders == null ? 0 : deliveredOrders)
                .deliveryInProgress(deliveryInProgress == null ? 0 : deliveryInProgress)
                .completedOrders(completedOrders == null ? 0 : completedOrders)
                .cancelledOrders(cancelledOrders == null ? 0 : cancelledOrders)
                .allOrders(allOrders == null ? 0 : allOrders)
                .build();
        return overViewVO;
    }

    public DishOverViewVO getDishOverView() {
        Integer sold = dishMapper.countByStatus(StatusConstant.ENABLE);
        Integer discontinued = dishMapper.countByStatus(StatusConstant.DISABLE);

        return DishOverViewVO.builder()
                .sold(sold == null ? 0 : sold)
                .discontinued(discontinued == null ? 0 : discontinued)
                .build();
    }

    public SetmealOverViewVO getSetmealOverView() {
        Integer sold = setmealMapper.countByStatus(StatusConstant.ENABLE);
        Integer discontinued = setmealMapper.countByStatus(StatusConstant.DISABLE);

        return SetmealOverViewVO.builder()
                .sold(sold == null ? 0 : sold)
                .discontinued(discontinued == null ? 0 : discontinued)
                .build();
    }
}
