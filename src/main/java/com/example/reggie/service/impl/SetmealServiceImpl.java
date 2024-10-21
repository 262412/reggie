package com.example.reggie.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
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

    /**
     * 根据多个ID删除套餐及其关联的菜品
     * 此方法首先检查给定的套餐是否正在售卖中，如果是，则抛出异常，防止删除正在售卖的套餐
     * 如果套餐不在售卖中，则删除套餐表中的套餐记录以及关联的菜品记录
     *
     * @param ids 要删除的套餐ID列表
     * @throws RuntimeException 如果套餐正在售卖中，则抛出运行时异常
     */
    @Transactional
    public void removeWithDish(List<Long> ids) {
        //查询套餐状态，确定是否可以删除
        LambdaQueryWrapper<Setmeal> queryWrapper = new LambdaQueryWrapper();
        queryWrapper.in(Setmeal::getId,ids);
        queryWrapper.eq(Setmeal::getStatus,1);
        int count = (int) this.count(queryWrapper);
        if(count > 0){
            //如果不能删除，抛出业务异常
            throw new RuntimeException("套餐正在售卖中，不能删除");
        }
        //如果可以删除，先删除套餐表中的数据
        this.removeByIds(ids);
        //删除关系表中的数据
        LambdaQueryWrapper<SetmealDish> lambdaQueryWrapper = new LambdaQueryWrapper();
        lambdaQueryWrapper.in(SetmealDish::getSetmealId,ids);
        setmealDishService.remove(lambdaQueryWrapper);
    }
}
