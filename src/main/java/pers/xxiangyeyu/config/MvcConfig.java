package pers.xxiangyeyu.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import pers.xxiangyeyu.utils.LoginInterceptor;

@Configuration
public class MvcConfig implements WebMvcConfigurer {

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new LoginInterceptor())
                .excludePathPatterns(
                      "/shop/**",
                      "/voucher/**",
                      "/shop-type/**",
                      "/upload/**",
                      "/blog/hot",
                      "/user/code",
                      "/user/login"
                );
    }
}
