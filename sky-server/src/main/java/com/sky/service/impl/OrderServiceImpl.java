package com.sky.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.sky.context.BaseContext;
import com.sky.dto.OrdersDTO;
import com.sky.dto.OrdersPageQueryDTO;
import com.sky.dto.OrdersPaymentDTO;
import com.sky.dto.OrdersSubmitDTO;
import com.sky.entity.OrderDetail;
import com.sky.entity.Orders;
import com.sky.entity.ShoppingCart;
import com.sky.exception.OrderBusinessException;
import com.sky.exception.ShoppingCartBusinessException;
import com.sky.mapper.OrderMapper;
import com.sky.result.PageResult;
import com.sky.service.OrderService;
import com.sky.service.ShoppingCartService;
import com.sky.vo.OrderPaymentVO;
import com.sky.vo.OrderSubmitVO;
import com.sky.vo.OrderVO;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class OrderServiceImpl implements OrderService {

    @Autowired
    private ShoppingCartService shoppingCartService;

    @Autowired
    private OrderMapper orderMapper;


    @Transactional
    @Override
    public OrderSubmitVO submit(OrdersSubmitDTO ordersSubmitDTO) {

        //检查提交的订单是否存在地址
        Long addressBookId = ordersSubmitDTO.getAddressBookId();
        if (addressBookId == null){
            throw new OrderBusinessException("订单中没有地址");
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

        // 模拟支付：直接标记支付成功
        paySuccess(ordersPaymentDTO.getOrderNumber());

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

        // 根据订单id更新订单的状态、支付方式、支付状态、结账时间
        Orders orders = Orders.builder()
                .id(ordersDB.getId())
                .status(Orders.TO_BE_CONFIRMED)
                .payStatus(Orders.PAID)
                .checkoutTime(LocalDateTime.now())
                .build();
        List<Orders> ordersList = new ArrayList<>();
        orderMapper.batchUpdate(ordersList);
    }

    @Override
    public PageResult historyOrders(OrdersPageQueryDTO ordersPageQueryDTO) {

        int page = ordersPageQueryDTO.getPage();
        int pageSize = ordersPageQueryDTO.getPageSize();
        PageHelper.startPage(page,pageSize);
        Page<OrderVO> ordersPage = orderMapper.historyOrders(ordersPageQueryDTO);

        List<OrderVO> orders = ordersPage.getResult();
        long total = ordersPage.getTotal();

        PageResult pageResult = new PageResult();
        pageResult.setRecords(orders);
        pageResult.setTotal(total);
        return pageResult;
    }
}
