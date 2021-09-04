package com.tanhua.sso;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.PropertySource;

/**
 * @program: my-tanhua
 * @description:
 * @author: HongXin
 * @create: 2021-08-26 19:35
 */

@MapperScan("com.tanhua.common.mapper")
@SpringBootApplication
@PropertySource({"classpath:application.properties"})
@ComponentScan(basePackages = {"com.tanhua"})
public class MyApplication {
    public static void main(String[] args) {
        SpringApplication.run(MyApplication.class,args);
    }
}
