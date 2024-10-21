package com.example.reggie.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.reggie.dto.SetmealDto;
import com.example.reggie.entity.Setmeal;
import com.example.reggie.entity.SetmealDish;
import com.example.reggie.mapper.SetmealMapper;
import com.example.reggie.service.SetmealDishService;
import com.example.reggie.service.SetmealService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class SetmealServiceImpl extends ServiceImpl<SetmealMapper, Setmeal> implements SetmealService {
    // 自动注入SetmealDishService，用于后续批量保存套餐菜品信息
    @Autowired
    private SetmealDishService setmealDishService;

    /**
     * 保存套餐信息及其对应的菜品信息
     *
     * @param setmealDto 套餐信息传输对象，包含套餐基本信息及其包含的菜品信息
     */
    @Transactional
    public void saveWithDish(SetmealDto setmealDto) {
        // 保存套餐基本信息
        this.save(setmealDto);

        // 获取套餐包含的菜品列表
        List<SetmealDish> setmealDishes = setmealDto.getSetmealDishes();

        // 为每道菜品设置套餐ID，建立关联关系
        setmealDishes.stream().map((item)->{
            item.setSetmealId(setmealDto.getId());
            return item;
        }).collect(Collectors.toList());

        // 批量保存套餐菜品信息
        setmealDishService.saveBatch(setmealDishes);
    }
}
