package com.tanhua.sso.enums;

import com.baomidou.mybatisplus.core.enums.IEnum;


/**
 * @program: my-tanhua
 * @description: 用户性别
 * @author: HongXin
 * @create: 2021-08-26 18:58
 */


public enum SexEnum implements IEnum<Integer> {

    MAN(1,"男"),
    WOMAN(2,"女"),
    UNKNOWN(3,"未知");

    private int value;
    private String desc;

    SexEnum(int value,String desc){
        this.value = value;
        this.desc = desc;
    }
    @Override
    public Integer getValue() {
        return this.value;
    }

    @Override
    public String toString() {
        return this.desc;
    }
}
