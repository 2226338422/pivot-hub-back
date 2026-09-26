package com.pivothub.system.config;

import org.springframework.beans.factory.annotation.Autowired;

import com.pivothub.system.intercept.SystemAuthInterceptor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebMvcConfig implements WebMvcConfigurer {
    @Autowired
    private SystemAuthInterceptor authInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(authInterceptor)
                .addPathPatterns("/system/user/**")
                .excludePathPatterns("/system/auth/**", "/v3/api-docs/**", "/swagger-ui/**");
    }
}
