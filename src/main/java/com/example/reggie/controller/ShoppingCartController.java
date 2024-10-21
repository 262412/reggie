package com.example.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.example.reggie.common.BaseContext;
import com.example.reggie.common.R;
import com.example.reggie.entity.ShoppingCart;
import com.example.reggie.service.ShoppingCartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/shoppingCart")
public class ShoppingCartController {
    // 自动注入ShoppingCartService用于操作购物车相关数据
    @Autowired
    private ShoppingCartService shoppingCartService;

    /**
     * 处理购物车添加商品请求
     * 根据用户ID和商品信息（菜品ID或套餐ID），更新购物车中的商品数量
     * 如果商品不在购物车中，则将其添加到购物车
     *
     * @param shoppingCart 包含用户购物车信息的实体对象，包括用户ID、菜品ID或套餐ID等
     * @return 返回更新后的购物车实体对象
     */
    @PostMapping("/add")
    public R<ShoppingCart> add(@RequestBody ShoppingCart shoppingCart) {
        // 获取当前用户ID
        Long currentId = BaseContext.getCurrentId();
        // 设置购物车商品的用户ID
        shoppingCart.setUserId(currentId);
        // 获取购物车商品的菜品ID
        Long dishiId = shoppingCart.getDishId();

        // 创建查询条件构造器
        LambdaQueryWrapper<ShoppingCart> queryWrapper = new LambdaQueryWrapper<>();
        // 查询条件：用户ID相等
        queryWrapper.eq(ShoppingCart::getUserId, currentId);
        // 如果菜品ID不为空，则添加菜品ID查询条件；否则添加套餐ID查询条件
        if (dishiId != null) {
            queryWrapper.eq(ShoppingCart::getDishId, dishiId);
        } else {
            queryWrapper.eq(ShoppingCart::getSetmealId, shoppingCart.getSetmealId());
        }

        // 根据查询条件获取购物车中的商品
        ShoppingCart cartServiceOne = shoppingCartService.getOne(queryWrapper);
        // 如果购物车中已存在该商品，则增加商品数量
        if (cartServiceOne != null) {
            Integer number = cartServiceOne.getNumber();
            cartServiceOne.setNumber(number + 1);
            shoppingCartService.updateById(cartServiceOne);
        } else {
            // 如果购物车中不存在该商品，则将商品添加到购物车，并设置商品数量为1
            shoppingCart.setNumber(1);
            shoppingCartService.save(shoppingCart);
            cartServiceOne = shoppingCart;
        }
        // 返回更新后的购物车商品信息
        return R.success(cartServiceOne);
    }

    /**
     * 获取当前用户的购物车列表
     * <p>
     * 此方法首先获取当前用户ID，然后根据该ID查询购物车表中属于当前用户的所有记录，
     * 并按照创建时间升序排序最后返回查询结果
     *
     * @return 返回一个包装了购物车列表的响应对象
     */
    @GetMapping("/list")
    public R<List<ShoppingCart>> list() {
        // 获取当前用户ID
        Long currentId = BaseContext.getCurrentId();

        // 创建查询条件对象，并设置查询条件为用户ID等于当前用户ID，按创建时间升序排序
        LambdaQueryWrapper<ShoppingCart> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ShoppingCart::getUserId, currentId);
        queryWrapper.orderByAsc(ShoppingCart::getCreateTime);

        // 执行查询并获取结果
        List<ShoppingCart> list = shoppingCartService.list(queryWrapper);

        // 返回查询结果
        return R.success(list);
    }

    /**
     * 处理清空购物车的请求
     * <p>
     * 该方法使用DELETE请求映射来清空当前用户的购物车所有商品
     *
     * @return 返回一个响应对象，包含清空购物车操作的结果信息
     */
    @DeleteMapping("/clean")
    public R<String> clean() {
        // 获取当前用户ID
        Long currentId = BaseContext.getCurrentId();

        // 创建查询条件对象，并设置查询条件为用户ID等于当前用户ID
        LambdaQueryWrapper<ShoppingCart> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ShoppingCart::getUserId, currentId);

        // 执行删除操作
        shoppingCartService.remove(queryWrapper);

        // 返回删除成功响应
        return R.success("清空购物车成功");
    }
}

