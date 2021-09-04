package com.tanhua.common.utils;

import com.tanhua.common.pojo.User;

/**
 * @program: my-tanhua
 * @description:
 * @author: HongXin
 * @create: 2021-09-03 10:05
 */


public class UserThreadLocal {
    private static final ThreadLocal<User> LOCAL = new ThreadLocal<>();

    private UserThreadLocal(){}

    /**
     * 将对象放入到ThreadLocal
     * @param user
     */
    public static void set(User user){
        LOCAL.set(user);
    }

    /**
     * 返回当前线程中的user对象
     * @return
     */
    public static User get(){
        return LOCAL.get();
    }

    /**
     * 删除当前线程中的user对象
     */
    public static void remove(){
        LOCAL.remove();
    }
}