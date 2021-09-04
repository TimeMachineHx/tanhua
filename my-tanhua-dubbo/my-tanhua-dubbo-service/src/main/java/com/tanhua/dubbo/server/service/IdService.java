package com.tanhua.dubbo.server.service;

import com.tanhua.dubbo.server.enums.IdType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

/**
 * @program: my-tanhua
 * @description:
 * @author: HongXin
 * @create: 2021-09-03 20:09
 */

//⽣成⾃增⻓的id，原理：使⽤redis的⾃增⻓值
@Service
public class IdService {
    @Autowired
    private RedisTemplate<String, String> redisTemplate;
    public Long createId(IdType idType) {
        String idKey = "TANHUA_ID_" + idType.toString();
        return this.redisTemplate.opsForValue().increment(idKey);
    }
}
