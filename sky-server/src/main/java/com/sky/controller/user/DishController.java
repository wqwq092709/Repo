package com.sky.controller.user;

import com.sky.mapper.DishMapper;
import com.sky.result.Result;
import com.sky.service.DishService;
import com.sky.vo.DishVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController("userDishController")
@RequestMapping("/user/dish")
public class DishController {

    @Autowired
    private DishMapper dishMapper;

    @GetMapping("/list")
    public Result<List<DishVO>> getListById(Integer categoryId){
        List<DishVO> dishVOList = dishMapper.getListByCategoryId(categoryId);
        return Result.success(dishVOList);
    }
}
