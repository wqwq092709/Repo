package com.sky.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.sky.constant.MessageConstant;
import com.sky.dto.DishDTO;
import com.sky.dto.DishPageQueryDTO;
import com.sky.entity.Dish;
import com.sky.entity.DishFlavor;
import com.sky.exception.DeletionNotAllowedException;
import com.sky.mapper.DishFlavorMapper;
import com.sky.mapper.DishMapper;
import com.sky.mapper.SetmealDishMapper;
import com.sky.result.PageResult;
import com.sky.service.DishService;
import com.sky.vo.DishVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
public class DishServiceImpl implements DishService {

    @Autowired
    private DishMapper dishMapper;

    @Autowired
    private DishFlavorMapper dishFlavorMapper;

    @Autowired
    private SetmealDishMapper setmealDishMapper;

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
        Page<DishVO> dishPage = dishMapper.page(dishPageQueryDTO);
        log.info("dishPage:{}",dishPage);

        long total = dishPage.getTotal();

        List<DishVO> result = dishPage.getResult();
        return new PageResult(total,result);
    }


    @Override
    public void startOrEnd(Dish dish) {
        dishMapper.startOrEnd(dish);
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

        //非空判断
        if (CollectionUtils.isEmpty(ids)) {
            return;
        }

        // 检查是否有起售中的菜品
        List<Dish> dishes = dishMapper.getByIds(ids);
        for (Dish dish : dishes) {
            if (dish.getStatus() == 1) {
                throw new DeletionNotAllowedException(MessageConstant.DISH_ON_SALE);
            }
        }

        //检查是否关联了套餐
        List<Long> setmealIds = setmealDishMapper.getSetmealIdsByDishIds(ids);
        if (setmealIds != null && !setmealIds.isEmpty()){
            throw new DeletionNotAllowedException(MessageConstant.DISH_BE_RELATED_BY_SETMEAL);
        }
        
        // 先删相关口味
        dishFlavorMapper.deleteByDishIds(ids);
        // 再删菜品
        dishMapper.deleteBatch(ids);
    }
}
