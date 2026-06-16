package com.sky.service;

import com.sky.dto.DishDTO;
import com.sky.entity.DishFlavor;

import java.util.List;

public interface DishFlavorService {
    /**
     * 批量插入口味
     * @param dishFlavorList
     */
    void insertBatch(List<DishFlavor> dishFlavorList);

    /**
     * 根据菜品id获取口味
     * @param id
     * @return
     */
    List<DishFlavor> getByDishId(long id);

    /**
     * 根据菜品删除口味
     * @param id
     */
    void deleteByDishId(long id);

    void update(DishDTO dishDTO);
}
