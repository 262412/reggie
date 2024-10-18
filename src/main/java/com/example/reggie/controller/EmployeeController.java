package com.example.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.example.reggie.common.R;
import com.example.reggie.entity.Employee;
import com.example.reggie.service.EmployeeService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.DigestUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.lang.invoke.LambdaConversionException;

@Slf4j
@RestController
@RequestMapping("/employee")
public class EmployeeController {

    // 自动注入EmployeeService，用于处理员工相关业务逻辑
    @Autowired
    private EmployeeService employeeService;

    /**
     * 处理登录请求
     *
     * @param request HTTP请求对象，用于获取会话信息
     * @param employee 包含用户名和密码的员工对象
     * @return 登录结果，包括成功或错误信息
     */
    @PostMapping("/login")
    public R<Employee> login(HttpServletRequest request, @RequestBody Employee employee){
        // 获取并加密密码，以提高安全性
        String password = employee.getPassword();
        password = DigestUtils.md5DigestAsHex(password.getBytes());

        // 创建查询包装器，用于根据用户名查询员工信息
        LambdaQueryWrapper<Employee> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Employee::getUsername, employee.getUsername());

        // 使用EmployeeService查询员工信息
        Employee emp = employeeService.getOne(queryWrapper);

        // 验证员工是否存在
        if (emp == null){
            return R.error("登录失败");
        }

        // 验证密码是否匹配
        if (!emp.getPassword().equals(password)){
            return R.error("登录失败");
        }

        // 检查账号是否已禁用
        if (emp.getStatus() == 0){
            return R.error("账号已禁用");
        }

        // 登录成功，将员工信息存入会话
        request.getSession().setAttribute("employee", emp.getId());

        // 返回登录成功结果
        return R.success(emp);
    }

    /**
     * 处理登出请求
     *
     * @param request HTTP请求对象，用于移除会话信息
     * @return 登出结果，包括成功信息
     */
    @PostMapping("/logout")
    public R<String> logout(HttpServletRequest request){
        // 从会话中移除员工信息，实现登出
        request.getSession().removeAttribute("employee");

        // 返回登出成功结果
        return R.success("退出成功");
    }
}
