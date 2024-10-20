package com.example.reggie.controller;

import com.example.reggie.common.R;
import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.UUID;

@RestController
@RequestMapping("/common")
public class CommonController {
    // 从配置文件中获取文件上传的基础路径
    @Value("${reggie.path}")
    private String basePath;

    /**
     * 文件上传接口
     * 接收一个MultipartFile类型的文件作为参数
     * 返回一个表示上传结果的字符串
     */
    @PostMapping
    public R<String> upload(MultipartFile file) throws IOException {
        // 获取文件的原始名称
        String originalFilename = file.getOriginalFilename();
        // 获取文件的后缀名
        String suffix = originalFilename.substring(originalFilename.lastIndexOf("."));
        // 使用UUID生成一个新的文件名，以避免文件名冲突，并保留原始后缀
        String s = UUID.randomUUID().toString() + suffix;
        // 创建一个File对象，指向上传文件的基础目录
        File dir = new File(basePath);
        // 如果目录不存在，则创建该目录
        if (!dir.exists()) {
            dir.mkdirs();
        }
        try {
            // 将上传的文件转移到指定的路径下
            file.transferTo(new File(basePath + originalFilename));
        } catch (IOException e) {
            // 如果发生IOException，抛出一个运行时异常
            throw new RuntimeException(e);
        } catch (IllegalStateException e) {
            // 如果发生IllegalStateException，打印异常信息
            e.printStackTrace();
        }
        // 返回上传成功的响应
        return R.success("上传成功");
    }

    /**
     * 文件下载接口
     * 接收一个文件名作为参数，用于指定要下载的文件
     * 通过HttpServletResponse对象将文件写入响应流中
     */
    @GetMapping("/download")
    public void download(String name, HttpServletResponse response) throws IOException {
        try {
            // 创建一个FileInputStream对象，用于读取要下载的文件
            FileInputStream fileInputStream = new FileInputStream(new File(basePath + name));
            // 获取响应的输出流
            ServletOutputStream outputStream = response.getOutputStream();
            // 设置响应的内容类型为image/jpeg
            response.setContentType("image/jpeg");
            // 用于读取文件的缓冲区
            int len = 0;
            byte[] bytes = new byte[1024];
            // 循环读取文件内容，并写入响应流中
            while ((len = fileInputStream.read(bytes)) != -1) {
                outputStream.write(bytes, 0, len);
                outputStream.flush();
            }
            // 关闭输出流和输入流
            outputStream.close();
            fileInputStream.close();
        } catch (FileNotFoundException e) {
            // 如果文件未找到，抛出一个运行时异常
            throw new RuntimeException(e);
        }
    }
}

