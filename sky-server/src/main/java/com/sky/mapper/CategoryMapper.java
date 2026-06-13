package com.sky.mapper;


import com.github.pagehelper.Page;
import com.sky.dto.CategoryDTO;
import com.sky.dto.CategoryPageQueryDTO;
import com.sky.entity.Category;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;


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

    void update(CategoryDTO categoryDTO);

    @Insert("insert into category (type, name, sort, status, create_time, update_time, create_user, update_user) " +
            "values (#{type}, #{name}, #{sort}, #{status}, #{createTime}, #{updateTime}, #{createUser}, #{updateUser})")
    void save(Category category);
}
