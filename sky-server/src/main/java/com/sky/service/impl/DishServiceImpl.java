package com.sky.service.impl;

import com.sky.dto.DishDTO;
import com.sky.entity.Dish;
import com.sky.entity.DishFlavor;
import com.sky.mapper.DishFlavorMapper;
import com.sky.mapper.DishMapper;
import com.sky.service.DishService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class DishServiceImpl implements DishService {

    @Autowired
    private DishMapper dishMapper;

    @Autowired
    private DishFlavorMapper dishFlavorMapper;

    //事务管理
    @Transactional
    @Override
    public void insert(DishDTO dishDTO) {

        //转换成dish
        Dish dish = new Dish();
        BeanUtils.copyProperties(dishDTO,dish);

        //插入菜品
        dishMapper.insert(dish);

        //获取返回的自增id
        Long dishId = dish.getId();

        //插入口味
        List<DishFlavor> flavors = dishDTO.getFlavors();
        if(flavors != null && !flavors.isEmpty()){

            flavors.forEach(flavor->flavor.setDishId(dishId));
            dishFlavorMapper.insertBatch(flavors);
        }

    }
}
