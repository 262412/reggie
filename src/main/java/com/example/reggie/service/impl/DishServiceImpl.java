package com.example.reggie.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.reggie.dto.DishDto;
import com.example.reggie.entity.Dish;
import com.example.reggie.entity.DishFlavor;
import com.example.reggie.mapper.DishMapper;
import com.example.reggie.service.DishFlavorService;
import com.example.reggie.service.DishService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
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

    /**
     * 根据菜品ID获取菜品详情及其口味信息
     *
     * @param id 菜品ID，用于查询菜品及其对应的口味信息
     * @return DishDto 菜品详情及其口味信息的对象
     */
    @Override
    public DishDto getByIdWithFlavor(Long id) {
        // 根据ID查询菜品基本信息
        Dish dish = this.getById(id);

        // 创建一个DishDto对象用于封装菜品信息及其口味信息
        DishDto dishDto = new DishDto();

        // 将菜品基本信息复制到DishDto对象中
        BeanUtils.copyProperties(dish, dishDto);

        // 创建查询条件，用于查询与当前菜品关联的口味信息
        LambdaQueryWrapper<DishFlavor> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(DishFlavor::getDishId, dish.getId());

        // 根据查询条件获取与当前菜品关联的所有口味信息
        List<DishFlavor> flavors = dishFlavorService.list(queryWrapper);

        // 将查询到的口味信息设置到DishDto对象中
        dishDto.setFlavors(flavors);

        // 返回封装了菜品信息及其口味信息的DishDto对象
        return dishDto;
    }

    /**
     * 更新菜品信息，并同时更新与菜品关联的口味信息
     * 此方法首先更新菜品的基本信息，然后根据新的菜品信息更新或删除相关的口味信息
     *
     * @param dishDto 菜品数据传输对象，包含菜品基本信息及关联的口味信息
     */
    @Override
    public void updateWithFlavor(DishDto dishDto) {
        // 更新菜品基本信息
        this.updateById(dishDto);

        // 创建查询条件，用于查找与当前菜品关联的所有口味
        LambdaQueryWrapper<DishFlavor> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(DishFlavor::getDishId, dishDto.getId());

        // 删除与当前菜品关联的所有原有口味，以便后续重新保存更新后的口味
        dishFlavorService.remove(queryWrapper);

        // 获取菜品的口味列表，并为每个口味设置当前菜品ID，以建立关联
        List<DishFlavor> flavors = dishDto.getFlavors();
        flavors = flavors.stream().map((item) -> {
            item.setDishId(dishDto.getId());
            return item;
        }).collect(Collectors.toList());

        // 批量保存更新后的口味信息，与菜品重新建立关联
        dishFlavorService.saveBatch(flavors);
    }
}
