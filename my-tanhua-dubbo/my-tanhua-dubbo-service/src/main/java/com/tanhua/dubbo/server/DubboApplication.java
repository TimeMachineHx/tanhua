package com.tanhua.dubbo.server;

import com.alibaba.dubbo.config.spring.context.annotation.EnableDubbo;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * @program: my-tanhua
 * @description:
 * @author: HongXin
 * @create: 2021-08-30 15:36
 */

@SpringBootApplication
@EnableDubbo
public class DubboApplication {

    public static void main(String[] args) {
        SpringApplication.run(DubboApplication.class, args);
    }
}
