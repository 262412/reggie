package com.example.reggie.cache_demo.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.example.reggie.cache_demo.entity.UserCache;
import com.example.reggie.cache_demo.service.UserCacheService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/user")
@Slf4j
public class UserControllerCache {
    // 自动注入CacheManager，用于管理缓存
    @Autowired
    private CacheManager cacheManager;

    // 自动注入UserCacheService，用于处理用户缓存相关的业务逻辑
    @Autowired
    private UserCacheService userService;

    /**
     * 使用CachePut注解将方法返回值放入缓存
     * value：指定缓存的名称，此处为"userCache"
     * key：指定缓存的key，此处为用户ID
     * 这个方法在更新用户信息后，会将新的用户信息存入缓存
     */
    @CachePut(value = "userCache",key = "#user.id")
    @PostMapping
    public UserCache save(UserCache user){
        userService.save(user);
        return user;
    }

    /**
     * 使用CacheEvict注解清理指定缓存
     * value：指定缓存的名称，此处为"userCache"
     * key：指定缓存的key，此处为用户ID
     * 当删除用户时，同时清除缓存中的用户信息
     */
    @CacheEvict(value = "userCache",key = "#p0")
    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id){
        userService.removeById(id);
    }

    /**
     * 使用CacheEvict注解清理指定缓存
     * value：指定缓存的名称，此处为"userCache"
     * key：指定缓存的key，此处为用户ID
     * 这个方法在更新用户信息后，会清除缓存中的旧信息
     */
    @CacheEvict(value = "userCache",key = "#result.id")
    @PutMapping
    public UserCache update(UserCache user){
        userService.updateById(user);
        return user;
    }

    /**
     * 使用Cacheable注解在方法执行前查看缓存中是否有数据
     * value：指定缓存的名称，此处为"userCache"
     * key：指定缓存的key，此处为用户ID
     * unless：当结果为null时不缓存
     * 当获取用户信息时，首先查看缓存中是否有数据，如果有则直接返回缓存数据
     */
    @Cacheable(value = "userCache",key = "#id",unless = "#result == null")
    @GetMapping("/{id}")
    public UserCache getById(@PathVariable Long id){
        UserCache user = userService.getById(id);
        return user;
    }

    /**
     * 使用Cacheable注解在方法执行前查看缓存中是否有数据
     * value：指定缓存的名称，此处为"userCache"
     * key：指定缓存的key，此处为用户ID和名称的组合
     * 这个方法用于查询用户列表，根据用户ID和名称进行筛选
     */
    @Cacheable(value = "userCache",key = "#user.id + '_' + #user.name")
    @GetMapping("/list")
    public List<UserCache> list(UserCache user){
        LambdaQueryWrapper<UserCache> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(user.getId() != null,UserCache::getId,user.getId());
        queryWrapper.eq(user.getName() != null,UserCache::getName,user.getName());
        List<UserCache> list = userService.list(queryWrapper);
        return list;
    }
}
