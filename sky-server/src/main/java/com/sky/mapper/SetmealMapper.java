package com.sky.mapper;

import com.github.pagehelper.Page;
import com.sky.annotation.AutoFill;
import com.sky.dto.SetmealDTO;
import com.sky.dto.SetmealPageQueryDTO;
import com.sky.entity.Setmeal;
import com.sky.enumeration.OperationType;
import com.sky.vo.DishItemVO;
import com.sky.vo.SetmealVO;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface SetmealMapper {

    /**
     * 根据分类id查询套餐的数量
     * @param id
     * @return
     */
    @Select("select count(id) from setmeal where category_id = #{categoryId}")
    Integer countByCategoryId(Long id);

    /**
     * 根据分类id查询套餐
     * @param categoryId
     * @return
     */
    @Select("select * from setmeal where category_id = #{categoryId}")
    List<SetmealVO> list(long categoryId);

    /**
     * 根据套餐id查询菜品
     * @param setmealId
     * @return
     */
    @Select("select sd.name,sd.copies,d.image,d.name,d.description from setmeal_dish sd left join dish d " +
            "on sd.dish_id = d.id where sd.setmeal_id = #{setmealId}")
    List<DishItemVO> getDishItemBySetmealId(Integer setmealId);

    /**
     * 新增套餐
     * @param setmeal
     */
    @AutoFill(OperationType.INSERT)
    @Insert("insert into setmeal (category_id, name, price, status, description, image, create_time, update_time, create_user, update_user) " +
            "values (#{categoryId}, #{name}, #{price}, #{status}, #{description}, #{image}, #{createTime}, #{updateTime}, #{createUser}, #{updateUser})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    void insert(Setmeal setmeal);

    /**
     * 分页查询套餐
     * @param setmealPageQueryDTO
     * @return
     */
    Page<SetmealVO> pageSetmeal(SetmealPageQueryDTO setmealPageQueryDTO);

    /**
     * 根据id查询套餐（含分类名称）
     * @param id
     * @return
     */
    @Select("select s.*, c.name as categoryName from setmeal s left join category c on s.category_id = c.id where s.id = #{id}")
    SetmealVO getById(Long id);

    /**
     * 根据id查询套餐实体
     * @param id
     * @return
     */
    @Select("select * from setmeal where id = #{id}")
    Setmeal getSetmealById(long id);

    /**
     * 批量查询套餐（用于状态校验）
     * @param ids
     * @return
     */
    @Select("<script>select * from setmeal where id in " +
            "<foreach collection='list' item='id' open='(' close=')' separator=','>#{id}</foreach></script>")
    List<Setmeal> getByIds(List<Long> ids);

    /**
     * 更新套餐
     * @param setmealDTO
     */
    @AutoFill(OperationType.UPDATE)
    void update(SetmealDTO setmealDTO);

    /**
     * 批量删除套餐
     * @param ids
     */
    void deleteBatch(@Param("ids") List<Long> ids);

    /**
     * 启用或禁用套餐
     * @param setmeal
     */
    @AutoFill(OperationType.CHANGE_STATUS)
    @Update("update setmeal set status = #{status}, update_time = #{updateTime}, update_user = #{updateUser} where id = #{id}")
    void startOrEnd(Setmeal setmeal);

    /**
     * 根据状态统计套餐数量
     */
    @Select("select count(id) from setmeal where status = #{status}")
    Integer countByStatus(Integer status);
}
