package com.example.api.config;

import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.filter.ShallowEtagHeaderFilter;

@Configuration
public class EtagConfig {
    @Bean
    public FilterRegistrationBean<ShallowEtagHeaderFilter> etagHeaderFilter() {
        FilterRegistrationBean<ShallowEtagHeaderFilter> filter =
                new FilterRegistrationBean<>(new ShallowEtagHeaderFilter());
        filter.addUrlPatterns("/students/*");
        filter.setName("etagFilter");
        return filter;
    }
}
