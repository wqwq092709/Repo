package com.sky.service.impl;

import com.alibaba.fastjson.JSON;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.sky.context.BaseContext;

import com.sky.dto.OrdersPageQueryDTO;
import com.sky.dto.OrdersPaymentDTO;
import com.sky.dto.OrdersSubmitDTO;
import com.sky.entity.AddressBook;
import com.sky.entity.OrderDetail;
import com.sky.entity.Orders;
import com.sky.entity.ShoppingCart;
import com.sky.exception.OrderBusinessException;
import com.sky.exception.ShoppingCartBusinessException;
import com.sky.mapper.AddressBookMapper;
import com.sky.mapper.OrderMapper;
import com.sky.result.PageResult;
import com.sky.service.OrderService;
import com.sky.service.ShoppingCartService;
import com.sky.service.WebSocketServer;
import com.sky.vo.OrderPaymentVO;
import com.sky.vo.OrderSubmitVO;
import com.sky.vo.OrderVO;
import lombok.extern.log4j.Log4j;
import lombok.extern.log4j.Log4j2;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;

import org.springframework.data.redis.core.RedisTemplate;

@Slf4j
@Service
public class OrderServiceImpl implements OrderService {

    @Autowired
    private ShoppingCartService shoppingCartService;

    @Autowired
    private OrderMapper orderMapper;

    @Autowired
    private WebSocketServer webSocketServer;

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @Autowired
    private AddressBookMapper addressBookMapper;

    @Transactional
    @Override
    public OrderSubmitVO submit(OrdersSubmitDTO ordersSubmitDTO) {

        //检查店铺营业状态
        Integer shopStatus = (Integer) redisTemplate.opsForValue().get("SHOP_STATUS");
        if (shopStatus == null || shopStatus != 1) {
            throw new OrderBusinessException("店铺已打烊，无法下单");
        }

        //检查提交的订单是否存在地址
        Long addressBookId = ordersSubmitDTO.getAddressBookId();
        if (addressBookId == null){
            throw new OrderBusinessException("订单中没有地址");
        }

        //查询地址簿，填充用户信息
        AddressBook addressBook = addressBookMapper.getById(addressBookId);
        if (addressBook == null) {
            throw new OrderBusinessException("地址信息不存在");
        }

        //检查提交的订单购物车中是否存在商品
        Long userId = BaseContext.getCurrentId();
        ShoppingCart shoppingCart = new ShoppingCart();
        shoppingCart.setUserId(userId);
        List<ShoppingCart> shopList = shoppingCartService.list(shoppingCart);
        if (shopList == null || shopList.isEmpty()){
            throw new ShoppingCartBusinessException("购物车中无商品");
        }

        //存入订单表
        Orders orders = new Orders();
        LocalDateTime now = LocalDateTime.now();
        BeanUtils.copyProperties(ordersSubmitDTO,orders);

        orders.setStatus(Orders.PENDING_PAYMENT);
        orders.setPayStatus(Orders.UN_PAID);
        UUID uuid = UUID.randomUUID();
        String orderNumber = String.valueOf(uuid);
        orders.setNumber(orderNumber);
        orders.setPayMethod(1);
        orders.setTablewareStatus(1);
        orders.setDeliveryStatus(1);
        orders.setUserId(userId);
        orders.setOrderTime(now);
        orders.setEstimatedDeliveryTime(ordersSubmitDTO.getEstimatedDeliveryTime());
        // 从地址簿填充用户信息
        orders.setConsignee(addressBook.getConsignee());
        orders.setPhone(addressBook.getPhone());
        orders.setAddress(addressBook.getProvinceName() + addressBook.getCityName()
                + addressBook.getDistrictName() + addressBook.getDetail());
        orders.setUserName(addressBook.getConsignee());
        orderMapper.submit(orders);

        //存入订单明细表
        Long orderId = orders.getId();
        List<OrderDetail> orderDetailList = new ArrayList<>();
        shopList.forEach(shop ->{
            OrderDetail orderDetail = new OrderDetail();
            BeanUtils.copyProperties(shop,orderDetail);
            orderDetail.setOrderId(orderId);
            orderDetailList.add(orderDetail);
        });
        orderMapper.submitToDetail(orderDetailList);

        //清空购物车
        shoppingCartService.deleteAll(userId);

        //返回结果
        BigDecimal ordersAmount = orders.getAmount();
        LocalDateTime orderTime = orders.getOrderTime();

        return   OrderSubmitVO.builder()
                .id(orderId)
                .orderAmount(ordersAmount)
                .orderNumber(orderNumber)
                .orderTime(orderTime).build();
    }

    /**
     * 订单支付
     *
     * @param ordersPaymentDTO
     * @return
     */
    public OrderPaymentVO payment(OrdersPaymentDTO ordersPaymentDTO) throws Exception {

        UUID RandomUserId = UUID.randomUUID();
        // 模拟支付：直接标记支付成功
        paySuccess(ordersPaymentDTO.getOrderNumber());

        //推送来单提醒
        Map<String,Object> message = new HashMap<>();
        message.put("type",1);
        message.put("content","订单号:"+ordersPaymentDTO.getOrderNumber());
        String messageJson = JSON.toJSONString(message);
        webSocketServer.sendToAllClient(messageJson);

        return OrderPaymentVO.builder()
                .nonceStr("mock")
                .paySign("mock")
                .timeStamp(String.valueOf(System.currentTimeMillis()))
                .signType("RSA")
                .packageStr("prepay_id=mock")
                .build();
    }

    /**
     * 支付成功，修改订单状态
     *
     * @param outTradeNo
     */
    public void paySuccess(String outTradeNo) {

        // 根据订单号查询订单
        Orders ordersDB = orderMapper.getByNumber(outTradeNo);

        // 先查出完整订单，只更新需要的字段，避免batchUpdate把其他字段覆盖为null
        Orders orders = orderMapper.getById(Math.toIntExact(ordersDB.getId()));
        orders.setStatus(Orders.TO_BE_CONFIRMED);
        orders.setPayStatus(Orders.PAID);
        orders.setCheckoutTime(LocalDateTime.now());
        List<Orders> ordersList = new ArrayList<>();
        ordersList.add(orders);
        orderMapper.batchUpdate(ordersList);
    }

    /**
     * 查询历史订单
     * @param ordersPageQueryDTO
     * @return
     */
    @Override
    public PageResult historyOrders(OrdersPageQueryDTO ordersPageQueryDTO) {

        int page = ordersPageQueryDTO.getPage();
        int pageSize = ordersPageQueryDTO.getPageSize();
        PageHelper.startPage(page,pageSize);
        Page<OrderVO> ordersPage = orderMapper.historyOrders(ordersPageQueryDTO);

        List<OrderVO> orders = ordersPage.getResult();
        long total = ordersPage.getTotal();

        // 为每个订单拼接菜品字符串
        for (OrderVO orderVO : orders) {
            List<OrderDetail> detailList = orderVO.getOrderDetailList();
            if (detailList != null && !detailList.isEmpty()) {
                StringBuilder dishes = new StringBuilder();
                for (OrderDetail detail : detailList) {
                    dishes.append(detail.getName()).append("*").append(detail.getNumber()).append(";");
                }
                orderVO.setOrderDishes(dishes.toString());
            }
        }

        PageResult pageResult = new PageResult();
        pageResult.setRecords(orders);
        pageResult.setTotal(total);
        return pageResult;
    }

    /**
     * 根据订单id查询订单
     * @param orderId
     * @return
     */
    @Override
    public Orders getByOrderId(Integer orderId) {
        return orderMapper.getById(orderId);
    }

    /**
     * 查询订单详情（含订单菜品字符串）
     */
    @Override
    public OrderVO getOrderDetail(Long orderId) {
        Orders orders = orderMapper.getById(Math.toIntExact(orderId));
        if (orders == null) {
            throw new OrderBusinessException("订单不存在");
        }
        // 查询订单详情列表
        List<OrderDetail> orderDetailList = orderMapper.getDetailByOrderId(orderId);
        // 拼接菜品字符串
        StringBuilder dishes = new StringBuilder();
        if (orderDetailList != null && !orderDetailList.isEmpty()) {
            for (OrderDetail detail : orderDetailList) {
                dishes.append(detail.getName()).append("*").append(detail.getNumber()).append(";");
            }
        }
        OrderVO orderVO = new OrderVO();
        BeanUtils.copyProperties(orders, orderVO);
        orderVO.setOrderDetailList(orderDetailList);
        orderVO.setOrderDishes(dishes.toString());
        return orderVO;
    }

    /**
     * 接单：待接单(2) → 已接单/待派送(3)
     */
    @Override
    public void confirmOrder(Long orderId) {
        Orders orders = orderMapper.getById(Math.toIntExact(orderId));
        if (orders == null) {
            throw new OrderBusinessException("订单不存在");
        }
        if (!orders.getStatus().equals(Orders.TO_BE_CONFIRMED)) {
            throw new OrderBusinessException("只有待接单状态的订单才能接单");
        }
        orders.setStatus(Orders.CONFIRMED);
        List<Orders> ordersList = new ArrayList<>();
        ordersList.add(orders);
        orderMapper.batchUpdate(ordersList);
    }

    /**
     * 拒单：待接单(2) → 已取消(6)
     */
    @Override
    public void rejectOrder(Long orderId, String rejectionReason) {
        Orders orders = orderMapper.getById(Math.toIntExact(orderId));
        if (orders == null) {
            throw new OrderBusinessException("订单不存在");
        }
        if (!orders.getStatus().equals(Orders.TO_BE_CONFIRMED)) {
            throw new OrderBusinessException("只有待接单状态的订单才能拒单");
        }
        orders.setStatus(Orders.CANCELLED);
        orders.setRejectionReason(rejectionReason);
        orders.setCancelTime(LocalDateTime.now());
        List<Orders> ordersList = new ArrayList<>();
        ordersList.add(orders);
        orderMapper.batchUpdate(ordersList);
    }

    /**
     * 取消订单 → 已取消(6)
     */
    @Override
    public void cancelOrder(Long orderId, String cancelReason) {
        log.info("取消订单入参: orderId={}, cancelReason={}", orderId, cancelReason);
        Orders orders = orderMapper.getById(Math.toIntExact(orderId));
        if (orders == null) {
            throw new OrderBusinessException("订单不存在");
        }
        orders.setStatus(Orders.CANCELLED);
        orders.setCancelReason(cancelReason);
        orders.setCancelTime(LocalDateTime.now());
        log.info("取消订单写入前: cancelReason={}, cancelTime={}", orders.getCancelReason(), orders.getCancelTime());
        List<Orders> ordersList = new ArrayList<>();
        ordersList.add(orders);
        orderMapper.batchUpdate(ordersList);
        log.info("取消订单写入完成");
    }

    /**
     * 派送：待派送(3) → 派送中(4)
     */
    @Override
    public void deliveryOrder(Long orderId) {
        Orders orders = orderMapper.getById(Math.toIntExact(orderId));
        if (orders == null) {
            throw new OrderBusinessException("订单不存在");
        }
        if (!orders.getStatus().equals(Orders.CONFIRMED)) {
            throw new OrderBusinessException("只有待派送状态的订单才能派送");
        }
        orders.setStatus(Orders.DELIVERY_IN_PROGRESS);
        List<Orders> ordersList = new ArrayList<>();
        ordersList.add(orders);
        orderMapper.batchUpdate(ordersList);
    }

    /**
     * 完成：派送中(4) → 已完成(5)
     */
    @Override
    public void completeOrder(Long orderId) {
        Orders orders = orderMapper.getById(Math.toIntExact(orderId));
        if (orders == null) {
            throw new OrderBusinessException("订单不存在");
        }
        if (!orders.getStatus().equals(Orders.DELIVERY_IN_PROGRESS)) {
            throw new OrderBusinessException("只有派送中状态的订单才能完成");
        }
        orders.setStatus(Orders.COMPLETED);
        orders.setDeliveryTime(LocalDateTime.now());
        List<Orders> ordersList = new ArrayList<>();
        ordersList.add(orders);
        orderMapper.batchUpdate(ordersList);
    }
}
