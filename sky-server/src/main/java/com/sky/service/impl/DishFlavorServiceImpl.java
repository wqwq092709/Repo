package com.sky.service.impl;

import com.sky.dto.DishDTO;
import com.sky.entity.DishFlavor;
import com.sky.mapper.DishFlavorMapper;
import com.sky.service.DishFlavorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class DishFlavorServiceImpl implements DishFlavorService {

    @Autowired
    private DishFlavorMapper dishFlavorMapper;

    @Override
    public void insertBatch(List<DishFlavor> dishFlavorList) {
        dishFlavorMapper.insertBatch(dishFlavorList);
    }

    @Override
    public List<DishFlavor> getByDishId(long id) {
        List<DishFlavor> dishFlavorList = dishFlavorMapper.getByDishId(id);
        return dishFlavorList;
    }

    @Override
    public void deleteByDishIds(List<Long> ids) {
        dishFlavorMapper.deleteByDishIds(ids);
    }

    @Override
    public void update(DishDTO dishDTO) {
        Long dishId = dishDTO.getId();

        //若口味为空则删除全部的相关口味
        if(dishDTO.getFlavors().isEmpty()) {

            List<Long> ids = new ArrayList<>();
            ids.add(dishId);
            dishFlavorMapper.deleteByDishIds(ids);

        }else {

            //更新口味
            List<DishFlavor> flavors = dishDTO.getFlavors();
            flavors.forEach(flavor -> flavor.setDishId(dishId));
            dishFlavorMapper.update(flavors);
        }

    }


}
