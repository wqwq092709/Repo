package com.sky.service;


import com.sky.dto.ShoppingCartDTO;
import com.sky.entity.ShoppingCart;

import java.util.List;

public interface ShoppingCartService {
    /**
     * 添加商品购物车
     * @param shoppingCartDTO
     */
    void add(ShoppingCartDTO shoppingCartDTO);

    /**
     * 查看购物车中的商品
     * @return
     */
    List<ShoppingCart> list(ShoppingCart shoppingCart);

    /**
     * 根据id删除购物车中的商品
     *@return
     */
    void deleteById(ShoppingCartDTO shoppingCartDTO);

    /**
     * 清空购物车
     */
    void deleteAll();
}
