package com.sky.mapper;

import com.sky.entity.SetmealDish;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface SetmealDishMapper {

    /**
     * 查询菜品是否关联套餐
     * @param ids
     * @return
     */
    List<Long> getSetmealIdsByDishIds(@Param("ids") List<Long> ids);

    /**
     * 批量插入套餐菜品关系
     * @param setmealDishes
     */
    void insertBatch(@Param("setmealDishes") List<SetmealDish> setmealDishes);

    /**
     * 根据套餐id列表批量删除套餐菜品关系
     * @param setmealIds
     */
    void deleteBySetmealIds(@Param("setmealIds") List<Long> setmealIds);

    /**
     * 根据套餐id查询关联的菜品
     * @param setmealId
     * @return
     */
    List<SetmealDish> getBySetmealId(@Param("setmealId") Long setmealId);

}
