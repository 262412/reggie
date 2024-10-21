package com.example.reggie.controller;

import com.example.reggie.common.R;
import com.example.reggie.dto.DishDto;
import com.example.reggie.service.DishFlavorService;
import com.example.reggie.service.DishService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
}

