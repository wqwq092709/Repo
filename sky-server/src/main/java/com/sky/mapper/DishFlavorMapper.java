package com.sky.mapper;

import com.sky.annotation.AutoFill;
import com.sky.entity.DishFlavor;
import com.sky.enumeration.OperationType;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface DishFlavorMapper {

    /**
     * 批量添加口味
     * @param dishFlavorList
     */
    @AutoFill(OperationType.INSERT)
    void insertBatch(List<DishFlavor> dishFlavorList);

    /**
     * 根据菜品id查询口味
     * @param dishId
     * @return
     */
    @Select("select * from dish_flavor where dish_id = #{dishId}")
    List<DishFlavor> getByDishId(long dishId);

    /**
     * 根据菜品id批量删除口味
     * @param ids
     */

    void deleteByDishIds(List<Long> ids);

    /**
     * 更新菜品口味
     * @param dishFlavors
     */
    @AutoFill(OperationType.UPDATE)
    void update(List<DishFlavor> dishFlavors);

}
