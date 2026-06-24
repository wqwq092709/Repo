package com.sky.controller.user;

import com.sky.context.BaseContext;
import com.sky.dto.ShoppingCartDTO;
import com.sky.entity.ShoppingCart;
import com.sky.result.Result;
import com.sky.service.ShoppingCartService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@Api(tags = "用户端购物车管理")
@RestController
@RequestMapping("user/shoppingCart")
public class ShoppingCartController {

    @Autowired
    private ShoppingCartService shoppingCartService;

    @ApiOperation("添加购物车")
    @PostMapping("/add")
    public Result add(@RequestBody ShoppingCartDTO shoppingCartDTO){
        shoppingCartService.add(shoppingCartDTO);
        return Result.success();
    }

    @ApiOperation("查看用户购物车")
    @GetMapping("list")
    public Result<List<ShoppingCart>> list(){
        log.info("请求到查看用户购物车");
        Long userId = BaseContext.getCurrentId();

        ShoppingCart shoppingCart = new ShoppingCart();
        shoppingCart.setUserId(userId);

        List<ShoppingCart> shoppingCarts = shoppingCartService.list(shoppingCart);
        return Result.success(shoppingCarts);
    }

    @PostMapping("/sub")
    public Result deleteById(@RequestBody ShoppingCartDTO shoppingCartDTO){
        log.info("请求到删除购物车中的商品");

        shoppingCartService.deleteById(shoppingCartDTO);
        return Result.success();
    }
}
