package com.itheima.xiaotuxian.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

@Configuration
public class GlobalCorsConfig {
    //添加到容器中管理
    @Bean
    public CorsFilter corsFilter() {
        var config = new CorsConfiguration();
        config.addAllowedOrigin("*");
        config.setAllowCredentials(false);
        config.addAllowedMethod("*");
        config.addAllowedHeader("*");
        config.addExposedHeader("Content-Type");
        config.addExposedHeader( "X-Requested-With");
        config.addExposedHeader("accept");
        config.addExposedHeader("Origin");
        config.addExposedHeader( "Access-Control-Request-Method");
        config.addExposedHeader("Access-Control-Request-Headers");

        var configSource = new UrlBasedCorsConfigurationSource();
        configSource.registerCorsConfiguration("/**", config);

        return new CorsFilter(configSource);
    }

}
