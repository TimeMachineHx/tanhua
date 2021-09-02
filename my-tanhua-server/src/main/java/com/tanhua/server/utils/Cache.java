package com.tanhua.server.utils;

import java.lang.annotation.*;

/**
 * @program: my-tanhua
 * @description: 自定义注解，被标记为Cache的Controller进行缓存，其他情况不进行缓存
 * @author: HongXin
 * @create: 2021-09-02 15:06
 */

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Cache {
    /**
     * 缓存时间，默认60秒
     */
    String time() default "60";
}
