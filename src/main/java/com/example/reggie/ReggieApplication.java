package com.example.reggie;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class ReggieApplication {

    /**
     * 程序的入口点
     * 使用Spring Boot框架的SpringApplication类来启动应用程序
     * 这个方法接收命令行参数作为输入，没有返回值
     *
     * @param args 命令行参数，用于在启动应用程序时传递参数
     */
    public static void main(String[] args) {
        SpringApplication.run(ReggieApplication.class, args);
    }

}
