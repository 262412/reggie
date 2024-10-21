package com.example.reggie.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.reggie.dto.DishDto;
import com.example.reggie.entity.Dish;
import com.example.reggie.entity.DishFlavor;
import com.example.reggie.mapper.DishMapper;
import com.example.reggie.service.DishFlavorService;
import com.example.reggie.service.DishService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class DishServiceImpl extends ServiceImpl<DishMapper, Dish> implements DishService {
    // 自动注入菜品风味服务，用于后续批量保存风味信息
    @Autowired
    private DishFlavorService dishFlavorService;

    /**
     * 同时保存菜品及其风味信息
     *
     * @param dishDto 菜品DTO，包含菜品基本信息及其对应的风味信息
     * 该方法首先保存菜品基本信息，然后处理并批量保存菜品的风味信息
     * 使用事务确保菜品信息和风味信息的一致性，要么同时成功保存，要么同时取消操作
     */
    @Transactional
    public void saveWithFlavor(DishDto dishDto) {
        // 保存菜品基本信息
        this.save(dishDto);

        // 获取刚保存的菜品ID，用于关联风味信息
        Long dishId = dishDto.getId();

        // 获取菜品的风味信息列表，并为每个风味信息设置菜品ID，建立关联关系
        List<DishFlavor> flavors = dishDto.getFlavors();
        flavors = flavors.stream().map((item) -> {
            item.setDishId(dishId);
            return item;
        }).collect(Collectors.toList());

        // 批量保存风味信息，提高效率
        dishFlavorService.saveBatch(flavors);
    }
}
