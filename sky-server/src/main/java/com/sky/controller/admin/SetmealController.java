package com.sky.controller.admin;


import com.sky.dto.SetmealPageQueryDTO;
import com.sky.result.PageResult;
import com.sky.result.Result;
import com.sky.service.SetmealService;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController("admin/SetmealController")
@RequestMapping("/admin/setmeal")
public class SetmealController {

    @Autowired
    private SetmealService setmealService;

    @ApiOperation("分页查询套餐")
    @GetMapping("page")
    public Result<PageResult> list(SetmealPageQueryDTO setmealPageQueryDTO){
        PageResult pageResult = setmealService.pageSetmeal(setmealPageQueryDTO);
        return Result.success(pageResult);
    }
    //TODO 后期增加新增套餐

}
