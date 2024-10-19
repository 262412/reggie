package com.example.reggie.common;

import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.reflection.MetaObject;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
@Slf4j
public class MyMetaObjectHandler implements MetaObjectHandler {
    /**
     * 在插入操作时自动填充公共字段
     *
     * @param metaObject 元对象，用于获取和设置数据库操作的数据
     */
    @Override
    public void insertFill(MetaObject metaObject) {
        // 记录插入操作自动填充开始的日志
        log.info("公共字段自动填充[insert]...");
        // 设置创建时间和更新时间为当前时间
        metaObject.setValue("createTime", LocalDateTime.now());
        metaObject.setValue("updateTime", LocalDateTime.now());
        // 设置创建人和更新人为当前用户ID
        metaObject.setValue("createUser", BaseContext.getCurrentId());
        metaObject.setValue("updateUser", BaseContext.getCurrentId());
    }

    /**
     * 在更新操作时自动填充公共字段
     *
     * @param metaObject 元对象，用于获取和设置数据库操作的数据
     */
    @Override
    public void updateFill(MetaObject metaObject) {
        // 记录更新操作自动填充开始的日志
        log.info("公共字段自动填充[update]...");
        // 获取并记录当前线程ID
        long id = Thread.currentThread().getId();
        log.info("线程id为：{}", id);
        // 仅设置更新时间和更新人，因为更新操作不需要修改创建时间和创建人
        metaObject.setValue("updateTime", LocalDateTime.now());
        metaObject.setValue("updateUser", BaseContext.getCurrentId());
    }
}

