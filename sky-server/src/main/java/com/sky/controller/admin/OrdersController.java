package com.sky.controller.admin;

import com.sky.dto.OrdersPageQueryDTO;
import com.sky.result.PageResult;
import com.sky.result.Result;
import com.sky.service.OrderService;
import com.sky.service.WorkspaceService;
import com.sky.vo.OrderOverViewVO;
import com.sky.vo.OrderVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@Api(tags = "管理端订单")
@RestController("adminOrdersController")
@RequestMapping("/admin/order")
public class OrdersController {

    @Autowired
    private OrderService orderService;

    @Autowired
    private WorkspaceService workspaceService;

    @ApiOperation("查询历史订单")
    @GetMapping("/conditionSearch")
    public Result<PageResult> historyOrders( OrdersPageQueryDTO ordersPageQueryDTO){
        log.info("请求到管理端Controller");
        PageResult pageResult = orderService.historyOrders(ordersPageQueryDTO);
        return Result.success(pageResult);
    }

    @ApiOperation("各状态订单数量统计")
    @GetMapping("/statistics")
    public Result<Map<String, Integer>> statistics(){
        OrderOverViewVO orderOverView = workspaceService.getOrderOverView();
        Map<String, Integer> map = new HashMap<>();
        map.put("toBeConfirmed", orderOverView.getWaitingOrders());
        map.put("confirmed", orderOverView.getDeliveredOrders());
        map.put("deliveryInProgress", orderOverView.getDeliveryInProgress());
        return Result.success(map);
    }

    @ApiOperation("查询订单详情")
    @GetMapping("/details/{orderId}")
    public Result<OrderVO> getDetail(@PathVariable Long orderId){
        log.info("查询订单详情:{}", orderId);
        OrderVO orderVO = orderService.getOrderDetail(orderId);
        return Result.success(orderVO);
    }

    @ApiOperation("接单")
    @PutMapping("/confirm")
    public Result confirm(@RequestBody Map<String, Long> param){
        log.info("接单:{}", param.get("id"));
        orderService.confirmOrder(param.get("id"));
        return Result.success();
    }

    @ApiOperation("拒单")
    @PutMapping("/rejection")
    public Result rejection(@RequestBody Map<String, String> param){
        log.info("拒单:{},原因:{}", param.get("id"), param.get("rejectionReason"));
        orderService.rejectOrder(Long.valueOf(param.get("id")), param.get("rejectionReason"));
        return Result.success();
    }

    @ApiOperation("取消订单")
    @PutMapping("/cancel")
    public Result cancel(@RequestBody Map<String, Object> param){
        log.info("取消订单请求体: {}", param);
        String cancelReason = param.get("cancelReason") != null ? param.get("cancelReason").toString() : null;
        log.info("取消订单: id={}, cancelReason={}", param.get("id"), cancelReason);
        orderService.cancelOrder(Long.valueOf(param.get("id").toString()), cancelReason);
        return Result.success();
    }

    @ApiOperation("派送订单")
    @PutMapping("/delivery/{orderId}")
    public Result delivery(@PathVariable Long orderId){
        log.info("派送订单:{}", orderId);
        orderService.deliveryOrder(orderId);
        return Result.success();
    }

    @ApiOperation("完成订单")
    @PutMapping("/complete/{orderId}")
    public Result complete(@PathVariable Long orderId){
        log.info("完成订单:{}", orderId);
        orderService.completeOrder(orderId);
        return Result.success();
    }
}
