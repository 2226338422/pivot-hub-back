package com.pivothub.business.config;

import org.springframework.beans.factory.annotation.Autowired;

import com.pivothub.business.intercept.BusinessAuthInterceptor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class BusinessWebMvcConfig implements WebMvcConfigurer {
    @Autowired
    private BusinessAuthInterceptor businessAuth;
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(businessAuth).addPathPatterns("/business/**")
                .excludePathPatterns("/business/feign/**").order(0);
    }
}
