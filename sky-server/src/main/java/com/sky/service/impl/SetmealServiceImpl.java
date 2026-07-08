package com.sky.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.sky.constant.MessageConstant;
import com.sky.constant.StatusConstant;
import com.sky.dto.SetmealDTO;
import com.sky.dto.SetmealPageQueryDTO;
import com.sky.entity.Dish;
import com.sky.entity.Setmeal;
import com.sky.entity.SetmealDish;
import com.sky.exception.DeletionNotAllowedException;
import com.sky.exception.SetmealEnableFailedException;
import com.sky.mapper.DishMapper;
import com.sky.mapper.SetmealDishMapper;
import com.sky.mapper.SetmealMapper;
import com.sky.result.PageResult;
import com.sky.service.SetmealService;
import com.sky.vo.SetmealVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class SetmealServiceImpl implements SetmealService {

    @Autowired
    private SetmealMapper setmealMapper;

    @Autowired
    private SetmealDishMapper setmealDishMapper;

    @Autowired
    private DishMapper dishMapper;

    @Transactional
    @Override
    public void save(SetmealDTO setmealDTO) {
        // 1. 转换DTO为实体
        Setmeal setmeal = new Setmeal();
        BeanUtils.copyProperties(setmealDTO, setmeal);

        // 2. 插入套餐
        setmealMapper.insert(setmeal);

        // 3. 获取返回的自增id
        Long setmealId = setmeal.getId();

        // 4. 插入套餐菜品关系
        List<SetmealDish> setmealDishes = setmealDTO.getSetmealDishes();
        if (setmealDishes != null && !setmealDishes.isEmpty()) {
            setmealDishes.forEach(sd -> sd.setSetmealId(setmealId));
            setmealDishMapper.insertBatch(setmealDishes);
        }
    }

    @Override
    public PageResult pageSetmeal(SetmealPageQueryDTO setmealPageQueryDTO) {
        int page = setmealPageQueryDTO.getPage();
        int pageSize = setmealPageQueryDTO.getPageSize();
        PageHelper.startPage(page, pageSize);

        Page<SetmealVO> setmealPage = setmealMapper.pageSetmeal(setmealPageQueryDTO);
        List<SetmealVO> pageResult = setmealPage.getResult();
        long total = setmealPage.getTotal();
        return new PageResult(total, pageResult);
    }

    @Override
    public void startOrEnd(Setmeal setmeal) {
        // 如果是启用操作，检查套餐内的所有菜品是否处于启售状态
        if (setmeal.getStatus().equals(StatusConstant.ENABLE)) {
            List<SetmealDish> setmealDishes = setmealDishMapper.getBySetmealId(setmeal.getId());
            if (setmealDishes != null && !setmealDishes.isEmpty()) {
                List<Long> dishIds = setmealDishes.stream()
                        .map(SetmealDish::getDishId)
                        .collect(Collectors.toList());
                List<Dish> dishes = dishMapper.getByIds(dishIds);
                for (Dish dish : dishes) {
                    if (StatusConstant.DISABLE.equals(dish.getStatus())) {
                        throw new SetmealEnableFailedException(MessageConstant.SETMEAL_ENABLE_FAILED);
                    }
                }
            }
        }

        setmealMapper.startOrEnd(setmeal);
    }

    @Transactional
    @Override
    public void update(SetmealDTO setmealDTO) {
        Long setmealId = setmealDTO.getId();

        // 1. 删除旧的套餐菜品关系
        List<Long> ids = new ArrayList<>();
        ids.add(setmealId);
        setmealDishMapper.deleteBySetmealIds(ids);

        // 2. 批量插入新的套餐菜品关系
        List<SetmealDish> setmealDishes = setmealDTO.getSetmealDishes();
        if (setmealDishes != null && !setmealDishes.isEmpty()) {
            setmealDishes.forEach(sd -> sd.setSetmealId(setmealId));
            setmealDishMapper.insertBatch(setmealDishes);
        }

        // 3. 更新套餐
        setmealMapper.update(setmealDTO);
    }

    @Override
    public SetmealVO getById(Long id) {
        // 1. 查询套餐基本信息
        SetmealVO setmealVO = setmealMapper.getById(id);

        // 2. 查询套餐关联的菜品
        List<SetmealDish> setmealDishes = setmealDishMapper.getBySetmealId(id);
        setmealVO.setSetmealDishes(setmealDishes);

        return setmealVO;
    }

    @Transactional
    @Override
    public void deleteBatch(List<Long> ids) {
        // 非空判断
        if (CollectionUtils.isEmpty(ids)) {
            return;
        }

        // 检查是否有起售中的套餐
        List<Setmeal> setmeals = setmealMapper.getByIds(ids);
        for (Setmeal setmeal : setmeals) {
            if (StatusConstant.ENABLE.equals(setmeal.getStatus())) {
                throw new DeletionNotAllowedException(MessageConstant.SETMEAL_ON_SALE);
            }
        }

        // 先删除套餐菜品关系
        setmealDishMapper.deleteBySetmealIds(ids);

        // 再删除套餐
        setmealMapper.deleteBatch(ids);
    }
}
