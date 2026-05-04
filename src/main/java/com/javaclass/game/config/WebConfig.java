package com.javaclass.game.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**") // 允許所有路徑
                .allowedOrigins("http://127.0.0.1:5500", "http://localhost:5500") // 允許 VS Code Live Server
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS") // 允許的請求方式
                .allowedHeaders("*")
                .allowCredentials(true);
    }
}