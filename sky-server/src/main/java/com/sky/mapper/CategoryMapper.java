package com.sky.mapper;


import com.github.pagehelper.Page;
import com.sky.annotation.AutoFill;
import com.sky.dto.CategoryPageQueryDTO;
import com.sky.entity.Category;
import com.sky.enumeration.OperationType;
import org.apache.ibatis.annotations.*;

import java.util.List;


@Mapper
public interface CategoryMapper {

    /**
     * 分页查询商品
     * @return
     */
    Page<Category> CategoryPageQuery(CategoryPageQueryDTO categoryPageQueryDTO);

    /**
     * 启用或禁用分类商品
     * @param status
     * @param id
     */
    @Update("update category set status = #{status} where id = #{id}")
    void startOrEnd(String status, String id);

    /**
     * 根据id查询分类
     * @param id
     */
    @Select("select * from category where id = #{id}")
    Category getById(long id);

    /**
     * 更新分类
     * @param category
     */
    @AutoFill(OperationType.UPDATE)
    void update(Category category);

    /**
     * 新增分类
     * @param category
     */
    @AutoFill(OperationType.INSERT)
    @Insert("insert into category (type, name, sort, status, create_time, update_time, create_user, update_user) " +
            "values (#{type}, #{name}, #{sort}, #{status}, #{createTime}, #{updateTime}, #{createUser}, #{updateUser})")
    void save(Category category);

    /**
     * 根据id删除分类
     * @param id
     */

    @Delete("delete from category where id = #{id}")
    void deleteById(long id);

    /**
     * 根据类型查询分类
     * @param type
     * @return
     */
    List<Category> list(Integer type);

}
