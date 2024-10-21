package com.example.reggie.controller;

import com.example.reggie.common.R;
import com.example.reggie.dto.SetmealDto;
import com.example.reggie.service.SetmealDishService;
import com.example.reggie.service.SetmealService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
}
