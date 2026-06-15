package com.sky.controller.admin;

import com.sky.dto.DishDTO;
import com.sky.result.Result;
import com.sky.service.DishService;
import io.swagger.annotations.Api;

import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Api(tags = "菜品管理")
@RestController
@RequestMapping("/admin/dish")
public class DishController {

    @Autowired
    private DishService dishService;
    @ApiOperation("添加菜品")
    @PostMapping
    public Result insert(@RequestBody DishDTO dishDTO){
        dishService.insert(dishDTO);
        return Result.success();
    }
}
