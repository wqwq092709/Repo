package com.sky.controller.user;

import com.alibaba.fastjson.JSON;
import com.sky.context.BaseContext;
import com.sky.dto.OrdersPageQueryDTO;
import com.sky.dto.OrdersPaymentDTO;
import com.sky.dto.OrdersSubmitDTO;
import com.sky.result.PageResult;
import com.sky.result.Result;
import com.sky.service.OrderService;
import com.sky.service.WebSocketServer;
import com.sky.vo.OrderPaymentVO;
import com.sky.vo.OrderSubmitVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@Api("用户订单功能")
@Slf4j
@RestController("userOrderController")
@RequestMapping("/user/order")
public class OrderController {

    @Autowired
    private OrderService orderService;

    @Autowired
    private WebSocketServer webSocketServer;

    /**
     * 提交订单
     * @param ordersSubmitDTO
     * @return
     */
    @ApiOperation("提交订单")
    @PostMapping("/submit")
    public Result<OrderSubmitVO> submit(@RequestBody OrdersSubmitDTO ordersSubmitDTO){

        log.info("请求到OrderController");
        OrderSubmitVO submitVO = orderService.submit(ordersSubmitDTO);

        return Result.success(submitVO);
    }
    /**
     * 订单支付
     *
     * @param ordersPaymentDTO
     * @return
     */
    @ApiOperation("订单支付")
    @PutMapping("/payment")
    public Result<OrderPaymentVO> payment(@RequestBody OrdersPaymentDTO ordersPaymentDTO) throws Exception {
        log.info("订单支付：{}", ordersPaymentDTO);
        OrderPaymentVO orderPaymentVO = orderService.payment(ordersPaymentDTO);
        log.info("生成预支付交易单：{}", orderPaymentVO);
        return Result.success(orderPaymentVO);
    }

    /**
     * 查询历史订单
     * @param ordersPageQueryDTO
     * @return
     */
    @ApiOperation("历史订单")
    @GetMapping("/historyOrders")
    public Result<PageResult> historyOrders(OrdersPageQueryDTO ordersPageQueryDTO){
        log.info("请求到历史订单historyOrders");
        long userId = BaseContext.getCurrentId();
        ordersPageQueryDTO.setUserId(userId);
        PageResult historyOrdersPage = orderService.historyOrders(ordersPageQueryDTO);
        return Result.success(historyOrdersPage);
    }

    /**
     * 用户催单
     * @param id
     * @return
     */
    @ApiOperation("用户催单")
    @GetMapping("/reminder/{id}")
    public void reminder(@PathVariable Integer id){
        Map<String, Object> message = new HashMap<>();
        message.put("type",2);
        message.put("content","订单号:"+id);
        String messageJson = JSON.toJSONString(message);

        webSocketServer.sendToAllClient(messageJson);

    }
}
