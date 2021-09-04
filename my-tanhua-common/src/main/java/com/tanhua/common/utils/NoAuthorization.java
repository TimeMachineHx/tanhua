package com.tanhua.common.utils;

import java.lang.annotation.*;

/**
 * @program: my-tanhua
 * @description:
 * @author: HongXin
 * @create: 2021-09-03 15:53
 */

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface NoAuthorization {
}
