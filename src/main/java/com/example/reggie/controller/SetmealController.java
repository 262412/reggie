package com.example.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.reggie.common.R;
import com.example.reggie.dto.SetmealDto;
import com.example.reggie.entity.Category;
import com.example.reggie.entity.Setmeal;
import com.example.reggie.service.CategoryService;
import com.example.reggie.service.SetmealDishService;
import com.example.reggie.service.SetmealService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/setmeal")
@Slf4j
public class SetmealController {
    // 自动注入SetmealService服务，用于处理套餐相关的业务逻辑
    @Autowired
    private SetmealService setmealService;
    // 自动注入SetmealDishService服务，用于处理套餐菜品相关的业务逻辑
    @Autowired
    private SetmealDishService setmealDishService;
    @Autowired
    private CategoryService categoryService;

    /**
     * 处理新增套餐的请求
     * 此方法通过接收SetmealDto对象，该对象包含了套餐及其关联菜品的信息，来完成套餐的新增操作
     * 使用@PostMapping注解指定此方法响应POST请求，通常用于创建新资源
     *
     * @param setmealDto 包含套餐及其关联菜品信息的传输对象
     * @return 返回一个表示操作结果的响应对象，包含成功消息
     */
    @PostMapping
    public R<String> save(@RequestBody SetmealDto setmealDto){
        // 记录日志，表明开始新增套餐的操作
        log.info("新增套餐");
        // 调用setmealService的saveWithDish方法，处理套餐及其关联菜品的保存操作
        setmealService.saveWithDish(setmealDto);
        // 返回成功响应，表示套餐新增成功
        return R.success("新增套餐成功");
    }
    /**
     * 处理套餐的分页查询请求
     *
     * @param page 当前页码
     * @param pageSize 每页记录数
     * @param name 套餐名称，用于模糊查询
     * @return 返回包含分页信息和套餐数据的响应对象
     */
    @GetMapping
    public R<Page> page(int page, int pageSize, String name){
        // 创建Page对象用于存储分页信息和查询结果
        Page<Setmeal> pageInfo = new Page<>(page, pageSize);
        // 创建Page对象用于存储转换后的分页信息和DTO对象
        Page<SetmealDto> dtoPage = new Page<>();
        // 创建LambdaQueryWrapper对象用于条件查询
        LambdaQueryWrapper <Setmeal> queryWrapper = new LambdaQueryWrapper<>();
        // 添加模糊查询条件和排序条件
        queryWrapper.like(name != null, Setmeal::getName, name);
        queryWrapper.orderByDesc(Setmeal::getUpdateTime);
        // 调用setmealService的page方法，处理套餐分页查询操作
        setmealService.page(pageInfo, queryWrapper);
        // 将分页信息和查询结果的属性复制到dtoPage，但不包括records属性
        BeanUtils.copyProperties(pageInfo, dtoPage, "records");
        // 获取查询结果的记录列表
        List<Setmeal> records = pageInfo.getRecords();
        // 将记录列表转换为SetmealDto列表，并设置分类名称
        List<SetmealDto> list = records.stream().map(item -> {
            SetmealDto setmealDto = new SetmealDto();
            BeanUtils.copyProperties(item, setmealDto);
            Long categoryId = item.getCategoryId();
            Category category = categoryService.getById(categoryId);
            if (category != null) {
                String categoryName = category.getName();
                setmealDto.setCategoryName(categoryName);
            }
            return setmealDto;
        }).collect(Collectors.toList());
        // 将转换后的列表设置到dtoPage中
        dtoPage.setRecords(list);
        // 返回包含分页信息和套餐数据的响应对象
        return R.success(dtoPage);
    }
}
