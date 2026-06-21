package com.sky.mapper;

import com.github.pagehelper.Page;
import com.sky.annotation.AutoFill;
import com.sky.dto.DishDTO;
import com.sky.dto.DishPageQueryDTO;
import com.sky.entity.Dish;
import com.sky.enumeration.OperationType;
import com.sky.vo.DishVO;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface DishMapper {

    /**
     * 根据分类id查询菜品数量
     * @param categoryId
     * @return
     */
    @Select("select count(id) from dish where category_id = #{categoryId}")
    Integer countByCategoryId(Long categoryId);

    /**
     * 新增菜品
     * @param dish
     */
    @AutoFill(OperationType.INSERT)
    @Insert("insert into dish (name, category_id, price, image, description, status, create_time, update_time, create_user, update_user) " +
            "values (#{name}, #{categoryId}, #{price}, #{image}, " +
            "#{description}, #{status}, #{createTime}, #{updateTime}, #{createUser}, #{updateUser})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    void insert(Dish dish);

    /**
     * 分页查询菜品
     * @param dishPageQueryDTO
     */
    Page<DishVO> page(DishPageQueryDTO dishPageQueryDTO);

    /**
     * 启用或禁用菜品
     * @param dish
     */
    @AutoFill(OperationType.CHANGE_STATUS)
    @Update("update dish set status = #{status}, update_time = #{updateTime}, update_user = #{updateUser} where id = #{id}")
    void startOrEnd(Dish dish);

    /**
     * 更新菜品
     * @param dishDTO
     */
    @AutoFill(OperationType.UPDATE)
    void update(DishDTO dishDTO);

    /**
     * 根据id查询菜品（回显用）
     * @param id
     * @return
     */
    @Select("select * from dish where id = #{id}")
    DishVO getById(long id);

    /**
     * 根据id列表批量查询（状态检查用）
     * @param ids
     * @return
     */
    @Select("<script>select * from dish where id in " +
            "<foreach collection='list' item='id' open='(' close=')' separator=','>#{id}</foreach></script>")
    List<Dish> getByIds(List<Long> ids);

    /**
     * 根据分类id查询已启用的菜品
     * @param categoryId
     * @return
     */
    @Select("select * from dish where status = 1 and category_id = #{categoryId}")
    List<Dish> getByCategoryId(Long categoryId);

    /**
     * 批量删除
     * @param ids
     */
    void deleteBatch(List<Long> ids);

}
