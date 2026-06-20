package com.sky.controller.user;

import com.sky.mapper.SetmealMapper;
import com.sky.result.Result;
import com.sky.vo.DishItemVO;
import com.sky.vo.DishVO;
import com.sky.vo.SetmealVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/user/setmeal")
public class SetmealController {

    @Autowired
    private SetmealMapper setmealMapper;

    @GetMapping("/list")
    public Result<List<SetmealVO>> list(@RequestParam("categoryId") Long categoryId){
        log.info("user/setmeal/list接口正常...");
        List<SetmealVO> setmealVOS = setmealMapper.list(categoryId);
        return Result.success(setmealVOS);
    }
    @GetMapping("/dish/{id}")
    public Result<List<DishItemVO>> getDishBySetmealId(@PathVariable Integer id){
        List<DishItemVO> dishVOS = setmealMapper.getDishItemBySetmealId(id);
        return Result.success(dishVOS);
    }
}
