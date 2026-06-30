package com.sky.mapper;

import com.sky.entity.OrderDetail;
import com.sky.entity.Orders;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface OrderMapper {
    /**
     * 提交订单
     * @param orders
     */
    void submit(Orders orders);

    /**
     * 批量提交订单明细
     * @param orderDetailList
     */
    void submitToDetail(List<OrderDetail> orderDetailList);
}
