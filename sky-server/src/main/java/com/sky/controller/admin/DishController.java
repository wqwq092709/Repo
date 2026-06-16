package com.sky.controller.admin;

import com.sky.dto.DishDTO;
import com.sky.dto.DishPageQueryDTO;
import com.sky.entity.Dish;
import com.sky.result.PageResult;
import com.sky.result.Result;
import com.sky.service.DishService;
import com.sky.vo.DishVO;
import io.swagger.annotations.Api;

import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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
    @ApiOperation("分页查询")
    @GetMapping("/page")
    public Result<PageResult> page(DishPageQueryDTO dishPageQueryDTO){

        PageResult pageResult = dishService.page(dishPageQueryDTO);
        return Result.success(pageResult);
    }
    @ApiOperation("启用或禁用菜品")
    @PostMapping("/status/{status}")
    public Result startOrEnd(@PathVariable Integer status,long id){

        DishDTO dishDTO = new DishDTO();
        dishDTO.setId(id);
        dishDTO.setStatus(status);

        dishService.startOrEnd(dishDTO);
        return Result.success();
    }
    @ApiOperation("更新菜品")
    @PutMapping
    public Result update(@RequestBody DishDTO dishDTO){
        dishService.update(dishDTO);
        return Result.success();
    }

    @ApiOperation("根据id查询菜品")
    @GetMapping("/{id}")
    public Result<DishVO> getById(@PathVariable long id){
        DishVO dishVO = dishService.getById(id);
        return Result.success(dishVO);
    }

    @ApiOperation("批量删除")
    @DeleteMapping
    public Result delete(@RequestParam List<Long> ids){
        dishService.deleteBatch(ids);
        return Result.success();
    }
}
