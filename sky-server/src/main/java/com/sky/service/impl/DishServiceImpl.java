package com.sky.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.sky.dto.DishDTO;
import com.sky.dto.DishPageQueryDTO;
import com.sky.entity.Dish;
import com.sky.entity.DishFlavor;
import com.sky.mapper.DishFlavorMapper;
import com.sky.mapper.DishMapper;
import com.sky.result.PageResult;
import com.sky.service.DishService;
import com.sky.vo.DishVO;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.concurrent.ConcurrentUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Slf4j
@Service
public class DishServiceImpl implements DishService {

    @Autowired
    private DishMapper dishMapper;

    @Autowired
    private DishFlavorMapper dishFlavorMapper;

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

    @Override
    public PageResult page(DishPageQueryDTO dishPageQueryDTO) {

        int page = dishPageQueryDTO.getPage();
        int pageSize = dishPageQueryDTO.getPageSize();

        //开启分页查询
        PageHelper.startPage(page,pageSize);
        Page<Dish> dishPage = dishMapper.page(dishPageQueryDTO);
        log.info("dishPage:{}",dishPage);

        long total = dishPage.getTotal();

        List<Dish> result = dishPage.getResult();
        return new PageResult(total,result);
    }

    @Override
    public void startOrEnd(DishDTO dishDTO) {
        dishMapper.startOrEnd(dishDTO);
    }

    @Transactional
    @Override
    public void update(DishDTO dishDTO) {
        Long dishId = dishDTO.getId();

        // 1. 删除旧口味
        List<Long> ids = new ArrayList<>();
        ids.add(dishId);
        dishFlavorMapper.deleteByDishIds(ids);

        // 2. 批量插入新口味
        List<DishFlavor> flavors = dishDTO.getFlavors();
        if (flavors != null && !flavors.isEmpty()) {
            flavors.forEach(flavor -> flavor.setDishId(dishId));
            dishFlavorMapper.insertBatch(flavors);
        }

        // 3. 更新菜品
        dishMapper.update(dishDTO);
    }

    @Override
    public DishVO getById(long id) {
        List<DishFlavor> dishFlavors = dishFlavorMapper.getByDishId(id);
        DishVO dishVO = dishMapper.getById(id);
        dishVO.setFlavors(dishFlavors);
        return dishVO;
    }

    @Transactional
    @Override
    public void deleteBatch(List<Long> ids) {
        if(CollectionUtils.isEmpty(ids)){
            return;
        }
        //先删相关口味
        dishFlavorMapper.deleteByDishIds(ids);
        //再删菜品
        dishMapper.deleteBatch(ids);

    }
}
