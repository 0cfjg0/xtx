package com.itheima.xiaotuxian.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurationSupport;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebmvcConfig implements WebMvcConfigurer {
    @Override
public void addResourceHandlers(ResourceHandlerRegistry registry) {

//    registry.addResourceHandler("swagger-ui.html")
//            .addResourceLocations("classpath:/META-INF/resources/");
//    registry.addResourceHandler("/webjars/**")
//            .addResourceLocations("classpath:/META-INF/resources/webjars/");
        //添加默认的静态资源访问路径
        registry.addResourceHandler("/**")
                .addResourceLocations("classpath:static/","classpath:META-IFA/resources/","classpath:resources/","classpath:public/","classpath:/");

}
}
