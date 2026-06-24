package com.sky.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.sky.dto.SetmealPageQueryDTO;
import com.sky.mapper.SetmealMapper;
import com.sky.result.PageResult;
import com.sky.service.SetmealService;
import com.sky.vo.SetmealVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SetmealServiceImpl implements SetmealService {

    @Autowired
    private SetmealMapper setmealMapper;

    @Override
    public PageResult pageSetmeal(SetmealPageQueryDTO setmealPageQueryDTO) {

        int page = setmealPageQueryDTO.getPage();
        int pageSize = setmealPageQueryDTO.getPageSize();
        PageHelper.startPage(page,pageSize);

        Page<SetmealVO> setmealPage = setmealMapper.pageSetmeal();
        List<SetmealVO> pageResult = setmealPage.getResult();
        long total = setmealPage.getTotal();
        return new PageResult(total,pageResult);
    }
}
