package com.sky.service;

import com.sky.dto.SetmealDTO;
import com.sky.dto.SetmealPageQueryDTO;
import com.sky.entity.Setmeal;
import com.sky.result.PageResult;
import com.sky.vo.SetmealVO;

import java.util.List;

public interface SetmealService {

    /**
     * 新增套餐
     * @param setmealDTO
     */
    void save(SetmealDTO setmealDTO);

    /**
     * 分页查询套餐
     * @param setmealPageQueryDTO
     * @return
     */
    PageResult pageSetmeal(SetmealPageQueryDTO setmealPageQueryDTO);

    /**
     * 启用或禁用套餐
     * @param setmeal
     */
    void startOrEnd(Setmeal setmeal);

    /**
     * 更新套餐
     * @param setmealDTO
     */
    void update(SetmealDTO setmealDTO);

    /**
     * 根据id查询套餐（含菜品列表，用于回显）
     * @param id
     * @return
     */
    SetmealVO getById(Long id);

    /**
     * 批量删除套餐
     * @param ids
     */
    void deleteBatch(List<Long> ids);
}
