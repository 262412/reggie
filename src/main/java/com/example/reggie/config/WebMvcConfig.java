package com.example.reggie.config;

import com.example.reggie.common.JacksonObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurationSupport;

import java.util.List;

@Slf4j
@Configuration
public class WebMvcConfig extends WebMvcConfigurationSupport {
    @Override
    protected void addResourceHandlers(ResourceHandlerRegistry registry) {
        log.info("开始进行静态资源映射...");
        registry.addResourceHandler("/backend/**").addResourceLocations("classpath:/backend/");
        registry.addResourceHandler("/front/**").addResourceLocations("classpath:/front/");
    }

    /**
     * 扩展HTTP消息转换器列表
     * 此方法用于向Spring的HTTP消息转换器列表中添加自定义的消息转换器，以支持特定类型的数据转换
     * @param converters HTTP消息转换器列表，用于处理HTTP请求和响应的转换工作
     */
    @Override
    protected void extendMessageConverters(List<HttpMessageConverter<?>> converters){
        log.info("扩展消息转换器...");
        // 创建一个CBOR格式的HTTP消息转换器
        MappingJackson2HttpMessageConverter messageConverter = new MappingJackson2HttpMessageConverter();
        // 设置自定义的ObjectMapper以支持特定的序列化和反序列化需求
        messageConverter.setObjectMapper(new JacksonObjectMapper());
        // 将自定义的消息转换器添加到列表的最前面，以优先处理对应的HTTP请求
        converters.add(0, messageConverter);
    }
}
