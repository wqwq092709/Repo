package com.sky.controller.admin;

import com.sky.context.BaseContext;
import com.sky.dto.OrdersPageQueryDTO;
import com.sky.result.PageResult;
import com.sky.result.Result;
import com.sky.service.OrderService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@Slf4j
@Api(tags = "管理端订单")
@RestController("adminOrdersController")
@RequestMapping("/admin/order")
public class OrdersController {

    @Autowired
    private OrderService orderService;

    @ApiOperation("查询历史订单")
    @GetMapping("/conditionSearch")
    public Result<PageResult> historyOrders( OrdersPageQueryDTO ordersPageQueryDTO){
        log.info("请求到管理端Controller");
        Long userId = BaseContext.getCurrentId();
        ordersPageQueryDTO.setUserId(userId);
        PageResult pageResult = orderService.historyOrders(ordersPageQueryDTO);

        return Result.success(pageResult);
    }
}
