package com.sky.controller.user;

import com.sky.exception.OrderBusinessException;
import com.sky.result.Result;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;

@Slf4j
@Api(tags = "商品管理接口")
@RestController("userShopController")
@RequestMapping("/user/shop")
public class ShopController {

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    public static final String KEY = "SHOP_STATUS";

    @ApiOperation("获取营业状态")
    @GetMapping("/status")
    public Result<Integer> getStatus() {
        Integer status = (Integer) redisTemplate.opsForValue().get(KEY);
        if(status == null){
            throw new OrderBusinessException("状态为空");
        }
        log.info("获取当前营业状态:{}",status == 1?"营业中":"打烊中");
        return Result.success(status);
    }
}
