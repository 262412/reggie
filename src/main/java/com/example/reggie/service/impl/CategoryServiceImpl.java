package com.example.reggie.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.reggie.common.CustomException;
import com.example.reggie.entity.Category;
import com.example.reggie.entity.Dish;
import com.example.reggie.entity.Setmeal;
import com.example.reggie.mapper.CategoryMapper;
import com.example.reggie.service.CategoryService;
import com.example.reggie.service.DishService;
import com.example.reggie.service.SetmealService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CategoryServiceImpl extends ServiceImpl<CategoryMapper, Category> implements CategoryService {
    @Autowired
    private DishService dishService;
    @Autowired
    private SetmealService setmealService;

    @Override
    public void remove(Long id) {
        // 创建Lambda查询包装器，用于查询菜品表
        LambdaQueryWrapper<Dish> dishLambdaQueryWrapper = new LambdaQueryWrapper<>();
        // 设置查询条件：根据分类ID等于传入的id
        dishLambdaQueryWrapper.eq(Dish::getCategoryId, id);
        // 调用dishService的count方法，统计满足条件的菜品数量
        long count1 = dishService.count(dishLambdaQueryWrapper);
        // 检查菜品数量是否大于0，如果大于0则表示当前分类下有关联的菜品
        if (count1 > 0) {
            // 抛出自定义异常，提示不能删除有关联菜品的分类
            throw new CustomException("当前分类下关联了菜品，不能删除");
        }

        // 创建Lambda查询包装器，用于查询套餐表
        LambdaQueryWrapper<com.example.reggie.entity.Setmeal> setmealLambdaQueryWrapper = new LambdaQueryWrapper<>();
        // 设置查询条件：根据分类ID等于传入的id
        setmealLambdaQueryWrapper.eq(Setmeal::getCategoryId, id);
        // 调用setmealService的count方法，统计满足条件的套餐数量
        long count2 = setmealService.count(setmealLambdaQueryWrapper);
        // 检查套餐数量是否大于0，如果大于0则表示当前分类下有关联的套餐
        if (count2 > 0) {
            // 抛出自定义异常，提示不能删除有关联套餐的分类
            throw new CustomException("当前分类下关联了套餐，不能删除");
        }

        // 调用父类的removeById方法，根据id删除分类
        super.removeById(id);
    }
}
