package com.example.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.reggie.common.R;
import com.example.reggie.entity.Employee;
import com.example.reggie.service.EmployeeService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.DigestUtils;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;


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
    /**
     * 处理POST请求以保存员工信息
     *
     * @param request HTTP请求对象，用于获取当前会话的员工ID
     * @param employee 员工对象，包含要保存的员工信息
     * @return 返回表示保存操作结果的字符串消息
     */
    @PostMapping
    public R<String> save(HttpServletRequest request, @RequestBody Employee employee){
        // 记录新增员工的日志信息
        log.info("新增员工，员工信息：{}", employee.toString());

        // 设置员工的密码为MD5加密后的"123456"
        employee.setPassword(DigestUtils.md5DigestAsHex("123456".getBytes()));

        // 设置员工的创建时间和更新时间为当前系统时间
        employee.setCreateTime(LocalDateTime.now());
        employee.setUpdateTime(LocalDateTime.now());

        // 从会话中获取当前员工ID并设置为新增员工的创建人和更新人
        Long empId = (Long) request.getSession().getAttribute("employee");
        employee.setCreateUser(empId);
        employee.setUpdateUser(empId);

        // 调用员工服务层接口的save方法保存员工信息
        employeeService.save(employee);

        // 返回成功保存员工信息的消息
        return R.success("新增员工成功");
    }
    /**
     * 根据页面编号、页面大小和员工姓名查询员工分页信息
     * 此方法用于处理GET请求，根据提供的页面编号、页面大小和可选的员工姓名进行分页查询
     *
     * @param page 页面编号，表示请求的页码
     * @param pageSize 页面大小，表示每页显示的记录数
     * @param name 员工姓名，用于模糊查询
     * @return 返回一个封装了分页查询结果的对象
     */
    @GetMapping("/page")
    public R<Page> page(int page, int pageSize, String name) {
        // 记录日志，输出请求参数信息
        log.info("page = {}, pageSize = {}, name = {}", page, pageSize, name);

        // 创建一个Page对象，用于封装分页查询的信息
        Page pageInfo = new Page(page, pageSize);

        // 创建一个LambdaQueryWrapper对象，用于构建查询条件
        LambdaQueryWrapper<Employee> queryWrapper = new LambdaQueryWrapper<>();

        // 如果name参数不为空，则添加模糊查询条件
        queryWrapper.like(StringUtils.isNotEmpty(name), Employee::getName, name);

        // 添加排序条件，按更新时间降序排列
        queryWrapper.orderByDesc(Employee::getUpdateTime);

        // 调用employeeService的page方法执行分页查询
        employeeService.page(pageInfo, queryWrapper);

        // 返回封装了分页查询结果的对象
        return R.success(pageInfo);
    }
    @PutMapping
    public R<String> update(HttpServletRequest request, @RequestBody Employee employee){
        // 记录更新员工信息的日志信息
        log.info(employee.toString());

        // 从会话中获取当前员工ID并设置为更新人的ID
        Long empId = (Long) request.getSession().getAttribute("employee");
        employee.setUpdateUser(empId);
        employee.setUpdateTime(LocalDateTime.now());

        // 调用员工服务层接口的updateById方法更新员工信息
        employeeService.updateById(employee);
        return R.success("员工信息修改成功");
    }
}
