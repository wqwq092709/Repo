package com.sky.controller.user;

import com.sky.entity.Dish;

import com.sky.result.Result;
import com.sky.service.DishService;
import com.sky.vo.DishVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.concurrent.TimeUnit;

@Api(tags = "用户端菜品管理")
@RestController("userDishController")
@RequestMapping("/user/dish")
public class DishController {

    @Autowired
    private DishService dishService;

    @Autowired
    private RedisTemplate<String,Object> redisTemplate;

    @Cacheable(value = "dishCache",key = "#categoryId")
    @ApiOperation("查询菜品和口味")
    @GetMapping("/list")
    public Result<List<DishVO>> getListById(Integer categoryId){
        //查询菜品
        Dish dish = new Dish();
        dish.setCategoryId((long)categoryId );
        List<DishVO> dishVOList = dishService.listWithFlavor(dish);

        return Result.success(dishVOList);
    }
}
