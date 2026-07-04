package com.sky.controller.user;

import com.alibaba.fastjson.JSON;
import com.sky.constant.MessageConstant;
import com.sky.context.BaseContext;
import com.sky.dto.OrdersPageQueryDTO;
import com.sky.dto.OrdersPaymentDTO;
import com.sky.dto.OrdersSubmitDTO;
import com.sky.entity.Orders;
import com.sky.exception.OrderBusinessException;
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
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;

import com.sky.exception.FrequentlyOperationException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Api(tags = "用户订单功能")
@Slf4j
@RestController("userOrderController")
@RequestMapping("/user/order")
public class OrderController {

    public static final String LIMIT_USERID = "limit_";
    public static Integer operationTimes = 0;
    @Autowired
    private OrderService orderService;

    @Autowired
    private WebSocketServer webSocketServer;

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    /**
     * 提交订单
     *
     * @param ordersSubmitDTO
     * @return
     */
    @ApiOperation("提交订单")
    @PostMapping("/submit")
    public Result<OrderSubmitVO> submit(@RequestBody OrdersSubmitDTO ordersSubmitDTO) {

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
     *
     * @param ordersPageQueryDTO
     * @return
     */
    @ApiOperation("历史订单")
    @GetMapping("/historyOrders")
    public Result<PageResult> historyOrders(OrdersPageQueryDTO ordersPageQueryDTO) {
        log.info("请求到历史订单historyOrders");
        long userId = BaseContext.getCurrentId();
        ordersPageQueryDTO.setUserId(userId);
        PageResult historyOrdersPage = orderService.historyOrders(ordersPageQueryDTO);
        return Result.success(historyOrdersPage);
    }

    /**
     * 用户催单
     *
     * @param id
     * @return
     */
    @ApiOperation("用户催单")
    @GetMapping("/reminder/{id}")
    public Result reminder(@PathVariable Integer id) {

        /**
         * 催单限流
         */
        Long userId = BaseContext.getCurrentId();
        String limitKey = LIMIT_USERID + userId;

        //计数器
        Long count = redisTemplate.opsForValue().increment(limitKey);

        //第一次调用设置过期时间
        if(count == 1){
            redisTemplate.expire(limitKey,1,TimeUnit.MINUTES);
        }

        if(count > 3){
            throw new FrequentlyOperationException("操作过于频繁，请稍后再试");
        }
        //检查订单是否存在
        Orders orderId = orderService.getByOrderId(id);
        if (orderId == null) {
            throw new OrderBusinessException(MessageConstant.ORDER_NOT_FOUND);
        }

        log.info("订单存在");
        Map<String, Object> message = new HashMap<>();
        message.put("type", 2);
        message.put("content", "订单号:" + id);
        String messageJson = JSON.toJSONString(message);

        webSocketServer.sendToAllClient(messageJson);

        return Result.success();
    }
}
