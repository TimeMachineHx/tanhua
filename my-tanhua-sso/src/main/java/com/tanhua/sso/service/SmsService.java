package com.tanhua.sso.service;

import com.tanhua.sso.vo.ErrorResult;
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
        String redisKey = "CHECK_CODE_" + phone;
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
