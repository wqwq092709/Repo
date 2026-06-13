package com.sky.service;

import com.sky.dto.CategoryDTO;
import com.sky.dto.CategoryPageQueryDTO;
import com.sky.entity.Category;
import com.sky.result.PageResult;

/**
 * 分类管理业务
 */
public interface CategoryService {

    /**
     * 分页查询分类管理
     * @param categoryPageQueryDTO
     * @return
     */
    PageResult pageQuery(CategoryPageQueryDTO categoryPageQueryDTO);

    /**
     * 启用禁用分类
     * @param status
     * @param id
     */
    void startOrEnd(String status, String id);

    /**
     * 根据id查询分类
     * @param id
     * @return
     */
    Category getById(long id);

    /**
     * 更新分类
     * @param categoryDTO
     */
    void update(CategoryDTO categoryDTO);

    void save(CategoryDTO categoryDTO);
}
