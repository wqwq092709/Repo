package com.sky.service.impl;

import com.sky.context.BaseContext;
import com.sky.dto.ShoppingCartDTO;
import com.sky.entity.Setmeal;
import com.sky.entity.ShoppingCart;
import com.sky.mapper.DishMapper;
import com.sky.mapper.SetmealMapper;
import com.sky.mapper.ShoppingCartMapper;
import com.sky.service.ShoppingCartService;
import com.sky.vo.DishVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
public class ShoppingCartServiceImpl implements ShoppingCartService {

    @Autowired
    private ShoppingCartMapper shoppingCartMapper;

    @Autowired
    private DishMapper dishMapper;

    @Autowired
    private SetmealMapper setmealMapper;

    @Override
    public void add(ShoppingCartDTO shoppingCartDTO) {

        //先查询添加的商品有没有在购物车中
        ShoppingCart shoppingCart = new ShoppingCart();
        BeanUtils.copyProperties(shoppingCartDTO,shoppingCart);
        shoppingCart.setUserId(BaseContext.getCurrentId());

        List<ShoppingCart> shoppingCarts = shoppingCartMapper.list(shoppingCart);
        //若存在于购物车就更新数量
        if(shoppingCarts != null && !shoppingCarts.isEmpty()){
            log.info("购物车中已有商品");
            ShoppingCart cart = shoppingCarts.get(0);
            cart.setNumber(cart.getNumber()+1);
            shoppingCartMapper.updateNumberById(cart);

            //否则就加入购物车
        }else {

            if(shoppingCartDTO.getDishId() != null){
                log.info("添加到购物车的是菜品");
                DishVO dish = dishMapper.getById(shoppingCartDTO.getDishId());
                shoppingCart.setName(dish.getName());
                shoppingCart.setImage(dish.getImage());
                shoppingCart.setAmount(dish.getPrice());
                shoppingCart.setNumber(1);
            }else if(shoppingCartDTO.getSetmealId() != null){
                log.info("添加到购物车的是套餐");
                Setmeal setmeal = setmealMapper.getSetmealById(shoppingCartDTO.getSetmealId());
                shoppingCart.setName(setmeal.getName());
                shoppingCart.setImage(setmeal.getImage());
                shoppingCart.setAmount(setmeal.getPrice());
                shoppingCart.setNumber(1);
            }

            log.info("开始添加商品到购物车");
            shoppingCart.setUserId(BaseContext.getCurrentId());
            shoppingCart.setCreateTime(LocalDateTime.now());
            shoppingCartMapper.add(shoppingCart);
        }

    }

    @Override
    public List<ShoppingCart> list(ShoppingCart shoppingCart) {
        List<ShoppingCart> shoppingCarts = shoppingCartMapper.list(shoppingCart);
        return shoppingCarts;
    }

    @Override
    public void deleteById(ShoppingCartDTO shoppingCartDTO) {

        //删除之前判断当前的菜品或套餐数量是否大于1
        ShoppingCart shoppingCart = new ShoppingCart();
        BeanUtils.copyProperties(shoppingCartDTO,shoppingCart);

        List<ShoppingCart> list = shoppingCartMapper.list(shoppingCart);
        ShoppingCart cart = list.get(0);
        if (cart.getNumber() > 1){
            log.info("当前套餐或菜品数量大于1");

            Long id = cart.getId();
            shoppingCart.setId(id);
            shoppingCart.setNumber(cart.getNumber()-1);
            log.info("开始更新购物车的菜品或套餐数量");
            shoppingCartMapper.updateNumberById(shoppingCart);
        }else {
            log.info("当前套餐或菜品数量小于1");

            log.info("开始移除当前菜品或套餐");
            shoppingCartMapper.deleteById(shoppingCartDTO);
        }

    }
}
