package com.sky.mapper;

import com.github.pagehelper.Page;
import com.sky.dto.OrdersPageQueryDTO;
import com.sky.entity.OrderDetail;
import com.sky.entity.Orders;
import com.sky.vo.OrderVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface  OrderMapper {
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

    /**
     * 根据订单号查询订单
     * @param orderNumber
     */
    @Select("select * from orders where number = #{orderNumber}")
    Orders getByNumber(String orderNumber);

    /**
     * 批量更新订单信息
     * @param ordersList
     */
    void batchUpdate(List<Orders> ordersList);

    /**
     * 历史订单查询
     * @param ordersPageQueryDTO
     */
    Page<OrderVO> historyOrders(OrdersPageQueryDTO ordersPageQueryDTO);

    /**
     * 查询超时订单
     * @return
     */
    List<Orders> getOutTimeOrder(Orders orders);

    /**
     * 查询订单一直处于派送状态
     * @return
     */
    List<Orders> getOrderIsDELIVERY(Orders orders);

    /**
     * 根据订单id查询订单
     * @param orderId
     * @return
     */
    @Select("select * from orders where id = #{orderId}")
    Orders getById(Integer orderId);
}
