package com.sxps.xsps.config;

import com.sxps.xsps.interceptor.JwtInterceptor;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@RequiredArgsConstructor
public class WebConfig implements WebMvcConfigurer {

    private final JwtInterceptor jwtInterceptor;

    /**
     * 只保留图片静态资源
     */
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        String imgPath = System.getProperty("user.dir") + "/imgs/";
        registry.addResourceHandler("/imgs/**")
                .addResourceLocations("file:" + imgPath);
    }

    /**
     * JWT 拦截器
     */
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(jwtInterceptor)
                .addPathPatterns("/api/**")
                .excludePathPatterns(
                        "/api/auth/wxLogin",
                        "/api/merchant/login",
                        "/api/merchant/refresh",
                        "/api/merchant/orders",
                        "/api/upload/**",
                        "/imgs/**",
                        "/favicon.ico",
                        "/api/category/list",      // 分类列表：游客可见
                        "/api/product/page",       // 商品列表：游客可见
                        "/api/product/detail/**",   // 商品详情：游客可见
                        "/api/product/recommend"
                );
    }
}
