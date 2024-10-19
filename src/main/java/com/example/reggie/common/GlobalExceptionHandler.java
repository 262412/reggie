package com.example.reggie.common;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.sql.SQLIntegrityConstraintViolationException;

@ControllerAdvice(annotations = {RestController.class, Controller.class})
@ResponseBody
@Slf4j
public class GlobalExceptionHandler {
    /**
     * 处理SQL完整性约束违反异常的处理器
     * 用于捕获和处理与数据库操作相关的特定异常，返回给前端相应的错误信息
     */
    @ExceptionHandler(SQLIntegrityConstraintViolationException.class)
    public R<String> exceptionHandler(SQLIntegrityConstraintViolationException ex) {
        // 记录异常信息，便于问题追踪和定位
        log.error(ex.getMessage());

        // 判断异常消息中是否包含特定的错误提示，以决定返回给用户的错误信息
        if (ex.getMessage().contains("Duplicate entry")) {
            // 解析异常消息，提取出重复的值
            String[] split = ex.getMessage().split(" ");
            // 构造并返回特定的错误信息，告知用户哪个值已存在
            String msg = split[2] + "已存在";
            return R.error(msg);
        }

        // 如果异常消息不匹配已知的错误类型，返回通用的错误信息
        return R.error("未知错误");
    }
    @ExceptionHandler(CustomException.class)
    public R<String> exceptionHandler(CustomException ex) {
        log.error(ex.getMessage());
        return R.error(ex.getMessage());
    }
}

