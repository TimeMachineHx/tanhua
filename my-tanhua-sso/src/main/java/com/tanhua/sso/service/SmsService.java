package com.tanhua.sso.service;

import com.tanhua.sso.vo.ErrorResult;
import com.tanhua.sso.vo.RedisKey;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;

/**
 * @program: my-tanhua
 * @description: 短信验证
 * @author: HongXin
 * @create: 2021-08-26 20:51
 */

@Service
@Slf4j
public class SmsService {
    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    public ErrorResult sendCheckCode(String phone) {
        //判断手机是否合法
        boolean matches = phone.matches("^(13[0-9]|14[01456879]|15[0-35-9]|16[2567]|17[0-8]|18[0-9]|19[0-35-9])\\d{8}$");
        if (!matches){
            return ErrorResult.builder().errCode("000003").errMessage("手机号码格式不对").build();
        }
        String redisKey = RedisKey.code + phone;
        //先判断该手机号发送的验证码是否还未失效
        if (redisTemplate.hasKey(redisKey)) {
            String msg = "上一次发送的验证码还未失效！";
            return ErrorResult.builder().errCode("000001").errMessage(msg).build();
        }
        String code = "123456";
        //相当于发送成功，并将值写入redis
        this.redisTemplate.opsForValue().set(redisKey, code, Duration.ofMinutes(5));
        return null;
    }
}
