package com.example.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.reggie.common.R;
import com.example.reggie.dto.DishDto;
import com.example.reggie.entity.Category;
import com.example.reggie.entity.Dish;
import com.example.reggie.service.CategoryService;
import com.example.reggie.service.DishFlavorService;
import com.example.reggie.service.DishService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/dish")
/**
 * 菜品控制器类，负责处理与菜品相关的HTTP请求
 */
public class DishController {

    // 自动注入菜品服务类，用于处理菜品相关的业务逻辑
    @Autowired
    private DishService dishService;

    // 自动注入菜品口味服务类，用于处理菜品口味相关的业务逻辑
    @Autowired
    private DishFlavorService dishFlavorService;
    @Autowired
    private CategoryService categoryService;

    /**
     * 处理新增菜品的请求
     * 该方法使用@PostMapping注解，表示它仅响应POST请求
     * 它接收一个DishDto对象作为参数，该对象包含菜品及其口味信息
     * @param dishDto 包含菜品及其口味信息的传输对象
     * @return 返回一个封装了结果信息的响应对象
     */
    @PostMapping
    public R<String> save(@RequestBody DishDto dishDto) {
        // 调用菜品服务类的保存方法，该方法会同时保存菜品及其口味信息
        dishService.saveWithFlavor(dishDto);
        // 返回成功响应，表示菜品新增成功
        return R.success("新增菜品成功");
    }

    // 定义一个处理分页查询请求的控制器方法
    @GetMapping("/page")
    public R<Page> page(int page, int pageSize, String name) {
        // 创建一个Page对象，用于存储分页查询的信息和结果
        Page<Dish> pageInfo = new Page<>(page, pageSize);
        Page<DishDto> dishDtoPage = new Page<>();
        // 创建一个LambdaQueryWrapper对象，用于构建查询条件和排序
        LambdaQueryWrapper <Dish> queryWrapper = new LambdaQueryWrapper<>();
        // 如果name参数不为空，则添加模糊查询条件
        queryWrapper.like(name != null, Dish::getName, name);
        // 添加按更新时间降序排序的条件
        queryWrapper.orderByDesc(Dish::getUpdateTime);
        // 调用菜品服务类的分页查询方法，返回一个包含分页信息的响应对象
        dishService.page(pageInfo, queryWrapper);
        // 返回一个表示操作成功的响应对象，包含分页查询结果
        // 将 pageInfo 的属性复制到 dishDtoPage 中，但排除 "records" 属性
        BeanUtils.copyProperties(pageInfo, dishDtoPage, "records");
        // 获取 pageInfo 中的记录列表
        List<Dish> records = pageInfo.getRecords();
        // 使用流将 records 中的每个 Dish 对象转换为 DishDto 对象
        List<DishDto> list = records.stream().map(item -> {
            // 创建一个新的 DishDto 对象
            DishDto dishDto = new DishDto();
            // 将当前 Dish 对象的属性复制到 dishDto 中
            BeanUtils.copyProperties(item, dishDto);
            // 获取当前 Dish 对象的类别 ID
            Long categoryId = item.getCategoryId();
            // 根据类别 ID 获取对应的 Category 对象
            Category category = categoryService.getById(categoryId);
            // 如果 Category 对象不为空，则获取其名称并设置到 dishDto 中
            if (category != null) {
                String categoryName = category.getName();
                dishDto.setCategoryName(categoryName);
            }
            // 返回转换后的 dishDto 对象
            return dishDto;
        // 将转换后的 DishDto 对象收集到一个新的列表中
        }).collect(Collectors.toList());
        // 将转换后的 DishDto 列表设置到 dishDtoPage 中
        dishDtoPage.setRecords(list);
        // 返回包含转换后数据的 dishDtoPage 对象
        return R.success(dishDtoPage);
    }
}

