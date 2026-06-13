package com.sky.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.sky.constant.StatusConstant;
import com.sky.context.BaseContext;
import com.sky.dto.CategoryDTO;
import com.sky.dto.CategoryPageQueryDTO;
import com.sky.entity.Category;
import com.sky.exception.DeletionNotAllowedException;
import com.sky.mapper.CategoryMapper;
import com.sky.mapper.DishMapper;
import com.sky.mapper.SetmealMapper;
import com.sky.result.PageResult;
import com.sky.service.CategoryService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 分类管理业务
 */
@Service
public class CategoryServiceImpl implements CategoryService {

    @Autowired
    private CategoryMapper categoryMapper;

    @Autowired
    private DishMapper dishMapper;

    @Autowired
    private SetmealMapper setmealMapper;

    /**
     * 分页查询分类商品
     * @param categoryPageQueryDTO
     * @return
     */
    @Override
    public PageResult pageQuery(CategoryPageQueryDTO categoryPageQueryDTO) {

        int page = categoryPageQueryDTO.getPage();
        int pageSize = categoryPageQueryDTO.getPageSize();
        PageHelper.startPage(page,pageSize);
        Page<Category> categoryPage = categoryMapper.CategoryPageQuery(categoryPageQueryDTO);

        long total = categoryPage.getTotal();
        List<Category> result = categoryPage.getResult();
        return new PageResult(total,result);
    }

    @Override
    public void startOrEnd(String status, String id) {
        categoryMapper.startOrEnd(status,id);
    }

    @Override
    public Category getById(long id) {
        Category category = categoryMapper.getById(id);
        return category;
    }

    @Override
    public void update(CategoryDTO categoryDTO) {
        Category category = new Category();
        BeanUtils.copyProperties(categoryDTO,category);

        category.setUpdateTime(LocalDateTime.now());
        category.setUpdateUser(BaseContext.getCurrentId());
        categoryMapper.update(category);
    }

    @Override
    public void save(CategoryDTO categoryDTO) {

        Category category = new Category();
        BeanUtils.copyProperties(categoryDTO,category);

        category.setStatus(StatusConstant.DISABLE);
        category.setCreateTime(LocalDateTime.now());
        category.setCreateUser(BaseContext.getCurrentId());
        categoryMapper.save(category);
    }

    @Override
    public void deleteById(long id) {

        //检查当前分类是否关联了菜品
        Integer dishCount = dishMapper.countByCategoryId(id);
        if(dishCount >0){
            throw new DeletionNotAllowedException("当前有菜品，不能被删除");
        }

        //检查当前分类是否关联了套餐
        Integer setmealCount = setmealMapper.countByCategoryId(id);
        if(setmealCount >0){
            throw new DeletionNotAllowedException("当前有套餐，不能被删除");
        }

        //删除分类
        categoryMapper.deleteById(id);

    }
}
