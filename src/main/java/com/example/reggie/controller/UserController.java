package com.example.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.example.reggie.common.R;
import com.example.reggie.entity.User;
import com.example.reggie.service.UserService;
import com.example.reggie.utils.SMSUtils;
import com.example.reggie.utils.ValidateCodeUtils;
import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/user")
@Slf4j
/**
 * 用户控制器类，处理与用户相关的HTTP请求
 */
public class UserController {

    // 自动注入用户服务接口，用于处理用户业务逻辑
    @Autowired
    private UserService userService;

    /**
     * 发送验证码短信到指定手机号码
     *
     * @param user 用户对象，包含手机号码信息
     * @param session HTTP会话，用于保存验证码
     * @return 返回表示短信发送成功或失败的结果
     */
    @PostMapping("/sendMsg")
    public R<String> sendMsg(@RequestBody User user, HttpSession session){
        // 获取用户输入的手机号码
        String phone = user.getPhone();

        // 检查手机号码是否不为空
        if(StringUtils.isNotEmpty(phone)){
            // 生成4位数字的验证码
            String code = ValidateCodeUtils.generateValidateCode4String(4);

            // 发送验证码短信
            SMSUtils.sendMessage("瑞吉外卖", "", phone, code);

            // 将验证码保存到会话中，以备后续验证
            session.setAttribute(phone, code);

            // 返回短信发送成功的响应
            return R.success("手机验证码短信发送成功");
        }

        // 如果手机号码为空，返回短信发送失败的响应
        return R.error("短信发送失败");
    }
    /**
     * 处理登录请求
     *
     * @param map 包含登录信息的映射，包括电话号码和验证码
     * @param session HTTP会话，用于存储用户信息
     * @return 返回一个响应对象，包含用户信息或错误信息
     */
    @PostMapping("/login")
    public R<User> login(@RequestBody Map map, HttpSession session){
        // 从请求体中获取用户输入的电话号码和验证码
        String phone = map.get("phone").toString();
        String code = map.get("code").toString();

        // 从会话中获取与电话号码关联的验证码
        Object codeInSession = session.getAttribute(phone);

        // 验证用户输入的验证码与会话中存储的验证码是否匹配
        if(codeInSession != null && codeInSession.equals(code)){
            // 判断当前用户是否为新用户，如果是新用户则自动注册
            LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(User::getPhone, phone);
            User user = userService.getOne(queryWrapper);

            // 如果用户不存在，则创建新用户并保存到数据库
            if(user == null){
                user = new User();
                user.setPhone(phone);
                user.setStatus(1);
                userService.save(user);
            }

            // 将用户信息存储到会话中，以表示用户已登录
            session.setAttribute("user", user.getId());

            // 返回成功登录的响应，包含用户信息
            return R.success(user);
        }

        // 如果验证码不匹配，返回登录失败的错误响应
        return R.error("登录失败");
    }
}

