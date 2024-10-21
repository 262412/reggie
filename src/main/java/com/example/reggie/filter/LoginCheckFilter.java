package com.example.reggie.filter;


import com.alibaba.fastjson2.JSON;
import com.example.reggie.common.BaseContext;
import com.example.reggie.common.R;
import jakarta.servlet.*;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.AntPathMatcher;

import java.io.IOException;

@Slf4j
@WebFilter(filterName = "loginCheckFilter", urlPatterns = "/*")
public class LoginCheckFilter implements Filter {
    // 使用AntPathMatcher进行路径匹配
    public static final AntPathMatcher PATH_MATCHER = new AntPathMatcher();

    /**
     * 过滤器的doFilter方法，用于处理或拦截请求
     * @param servletRequest Servlet请求对象，用于获取HTTP请求信息
     * @param servletResponse Servlet响应对象，用于向客户端发送响应
     * @param filterChain 过滤链对象，用于将请求传递给下一个过滤器或目标资源
     * @throws IOException 如果在执行过程中发生I/O错误
     * @throws ServletException 如果在执行过程中发生Servlet错误
     */
    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        // 将ServletRequest和ServletResponse转换为HttpServletRequest和HttpServletResponse
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;
        // 获取请求的URI
        String requestURI = request.getRequestURI();
        // 记录日志，拦截到请求
        log.info("拦截到请求：{}", requestURI);
        // 定义不需要处理的请求URL数组
        String[] urls = new String[]{
                "/employee/login",
                "/employee/logout",
                "/backend/**",
                "/front/**",
                "/common/**",
                "/user/sendMsg",
                "/user/login"
        };
        // 检查当前请求是否在定义的不需要处理的URL数组中
        boolean check = check(urls, requestURI);
        if (check){
            // 如果不需要处理，记录日志并继续执行过滤链
            log.info("本次请求{}不需要处理", requestURI);
            filterChain.doFilter(request, response);
            return;
        }
        // 检查用户是否已登录
        if(request.getSession().getAttribute("employee")!= null){
            // 如果用户已登录，记录日志并继续执行过滤链
            log.info("用户已登录，用户id为：{}", request.getSession().getAttribute("employee"));
            Long empId = (Long) request.getSession().getAttribute("employee");
            BaseContext.setCurrentId(empId);
            filterChain.doFilter(request, response);
            return;
        }
        if(request.getSession().getAttribute("user")!= null){
            // 如果用户已登录，记录日志并继续执行过滤链
            log.info("用户已登录，用户id为：{}", request.getSession().getAttribute("user"));
            Long userId = (Long) request.getSession().getAttribute("user");
            BaseContext.setCurrentId(userId);
            filterChain.doFilter(request, response);
            return;
        }
        // 如果用户未登录，记录日志并返回错误响应
        log.info("用户未登录");
        response.getWriter().write(JSON.toJSONString(R.error("NOTLOGIN")));
    }

    /**
     * 检查请求URI是否匹配给定的URL模式数组
     * @param urls URL模式数组，用于匹配请求URI
     * @param requestURI 请求的URI，用于与URL模式进行匹配
     * @return 如果请求URI匹配任何一个URL模式，则返回true，否则返回false
     */
    public boolean check(String[] urls, String requestURI){
        // 遍历URL模式数组
        for (String url : urls) {
            // 使用PATH_MATCHER匹配URL模式和请求URI
            boolean match = PATH_MATCHER.match(url, requestURI);
            if (match) {
                // 如果匹配成功，返回true
                return true;
            }
        }
        // 如果没有匹配成功的URL模式，返回false
        return false;
    }

}
