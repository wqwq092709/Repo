package com.sky.controller.user;

import com.sky.entity.Dish;

import com.sky.result.Result;
import com.sky.service.DishService;
import com.sky.vo.DishVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
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

    @ApiOperation("查询菜品和口味")
    @GetMapping("/list")
    public Result<List<DishVO>> getListById(Integer categoryId){

        //先查缓存，若没有再查数据库
        String key = "DISH_"+categoryId;

        @SuppressWarnings("unchecked")
        List<DishVO> dishVOList = (List<DishVO>) redisTemplate.opsForValue().get(key);
        if(dishVOList != null && !dishVOList.isEmpty() ){
            return Result.success(dishVOList);
        }

        //查询菜品
        Dish dish = new Dish();
        dish.setCategoryId((long)categoryId );
        dishVOList = dishService.listWithFlavor(dish);

        //缓存菜品数据
        if(dishVOList != null && !dishVOList.isEmpty() ){
            redisTemplate.opsForValue().set(key,dishVOList,30, TimeUnit.MINUTES);
        }
        return Result.success(dishVOList);
    }
}
