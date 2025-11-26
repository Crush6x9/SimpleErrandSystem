package com.errand.config;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@MapperScan("com.errand.mapper")
public class WebConfig implements WebMvcConfigurer {
    @Value("${file.upload-dir:./uploads/}")
    private String uploadDir;

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // 映射上传文件的URL路径到实际文件路径
        registry.addResourceHandler("/uploads/**").addResourceLocations("file:" + uploadDir);
    }
}
