package com.tanhua.server.config;

import com.tanhua.server.interceptor.RedisCacheInterceptor;
import com.tanhua.server.interceptor.UserTokenInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * @program: my-tanhua
 * @description: 注册拦截器到spring容器中
 * @author: HongXin
 * @create: 2021-09-02 16:57
 */

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Autowired
    private RedisCacheInterceptor redisCacheInterceptor;

    @Autowired
    private UserTokenInterceptor userTokenInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        //拦截器顺序
        registry.addInterceptor(userTokenInterceptor).addPathPatterns("/**");
        registry.addInterceptor(redisCacheInterceptor).addPathPatterns("/**");

    }
}
