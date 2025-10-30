package com.errand.config;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@MapperScan("com.errand.mapper")
public class MyBatisConfig {
}
