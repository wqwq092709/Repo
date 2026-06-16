package com.sky.service;

import com.sky.dto.DishDTO;
import com.sky.dto.DishPageQueryDTO;
import com.sky.entity.Dish;
import com.sky.result.PageResult;
import com.sky.vo.DishVO;

import java.util.List;


public interface DishService {
    /**
     * 新增菜品
     * @param dishDTO
     */
    void insert(DishDTO dishDTO);

    /**
     * 分页查询菜品
     */
    PageResult page(DishPageQueryDTO dishPageQueryDTO);

    /**
     * 启用或禁用菜品
     * @param dishDTO
     */
    void startOrEnd(DishDTO dishDTO);

    /**
     * 更新菜品
     * @param dishDTO
     */
    void update(DishDTO dishDTO);

    /**
     * 根据id查询菜品
     * @param id
     * @return
     */
    DishVO getById(long id);

    /**
     * 批量删除菜品
     * @param ids
     */
    void deleteBatch(List<Long> ids);
}
