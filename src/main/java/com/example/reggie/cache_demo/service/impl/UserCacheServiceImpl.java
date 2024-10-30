package com.example.reggie.cache_demo.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.reggie.cache_demo.entity.UserCache;
import com.example.reggie.cache_demo.mapper.UserMapperCache;
import com.example.reggie.cache_demo.service.UserCacheService;
import org.springframework.stereotype.Service;

@Service
public class UserCacheServiceImpl extends ServiceImpl<UserMapperCache, UserCache> implements UserCacheService {
}
