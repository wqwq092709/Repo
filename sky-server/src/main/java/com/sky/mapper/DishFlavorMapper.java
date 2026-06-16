package com.sky.mapper;

import com.sky.dto.DishDTO;
import com.sky.dto.DishPageQueryDTO;
import com.sky.entity.Dish;
import com.sky.entity.DishFlavor;

import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface DishFlavorMapper {

    /**
     * 批量添加口味
     * @param dishFlavorList
     */
    void insertBatch(List<DishFlavor> dishFlavorList);

    /**
     * 根据菜品id查询口味
     * @param dishId
     * @return
     */
    @Select("select * from dish_flavor where dish_id = #{dishId}")
    List<DishFlavor> getByDishId(long dishId);

    /**
     * 根据菜品id删除口味
     * @param id
     */
    @Delete("delete from dish_flavor where dish_id = #{id}")
    void delete(long id);

    /**
     * 更新菜品口味
     * @param dishFlavors
     */
    void update(List<DishFlavor> dishFlavors);

}
