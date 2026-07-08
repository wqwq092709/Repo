package com.sky.controller.admin;

import com.sky.dto.SetmealDTO;
import com.sky.dto.SetmealPageQueryDTO;
import com.sky.entity.Setmeal;
import com.sky.result.PageResult;
import com.sky.result.Result;
import com.sky.service.SetmealService;
import com.sky.vo.SetmealVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Api(tags = "套餐管理")
@RestController("admin/SetmealController")
@RequestMapping("/admin/setmeal")
public class SetmealController {

    @Autowired
    private SetmealService setmealService;

    private static final String CACHE_NAME = "setmealCache";

    @ApiOperation("新增套餐")
    @CacheEvict(value = CACHE_NAME, allEntries = true)
    @PostMapping
    public Result save(@RequestBody SetmealDTO setmealDTO) {
        setmealService.save(setmealDTO);
        return Result.success();
    }

    @ApiOperation("分页查询套餐")
    @GetMapping("/page")
    public Result<PageResult> page(SetmealPageQueryDTO setmealPageQueryDTO) {
        PageResult pageResult = setmealService.pageSetmeal(setmealPageQueryDTO);
        return Result.success(pageResult);
    }

    @ApiOperation("启用或禁用套餐")
    @CacheEvict(value = CACHE_NAME, allEntries = true)
    @PostMapping("/status/{status}")
    public Result startOrEnd(@PathVariable Integer status, Long id) {
        Setmeal setmeal = new Setmeal();
        setmeal.setStatus(status);
        setmeal.setId(id);
        setmealService.startOrEnd(setmeal);
        return Result.success();
    }

    @ApiOperation("更新套餐")
    @CacheEvict(value = CACHE_NAME, allEntries = true)
    @PutMapping
    public Result update(@RequestBody SetmealDTO setmealDTO) {
        setmealService.update(setmealDTO);
        return Result.success();
    }

    @ApiOperation("根据id查询套餐")
    @GetMapping("/{id}")
    public Result<SetmealVO> getById(@PathVariable Long id) {
        SetmealVO setmealVO = setmealService.getById(id);
        return Result.success(setmealVO);
    }

    @ApiOperation("批量删除套餐")
    @CacheEvict(value = CACHE_NAME, allEntries = true)
    @DeleteMapping
    public Result delete(@RequestParam List<Long> ids) {
        setmealService.deleteBatch(ids);
        return Result.success();
    }
}
