package com.tanhua.sso.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.tanhua.sso.mapper.UserMapper;
import com.tanhua.sso.pojo.User;
import com.tanhua.sso.vo.RedisKey;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

/**
 * @program: my-tanhua
 * @description:
 * @author: HongXin
 * @create: 2021-08-27 21:02
 */

@Service
public class LoginService {
    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    @Autowired
    private UserMapper userMapper;

    @Value("${jwt.secret}")
    private String secret;

    public Map<String, Object> loginVerification(String verificationCode, String phone) {
        boolean isNew = false;
        /**
         * 校验验证码
         */
        String redisKey = RedisKey.code + phone;
        String code = redisTemplate.opsForValue().get(redisKey);
        if (!StringUtils.equals(verificationCode, code)) {
            return null;
        }
        //验证码正确的话，需要删除redis中的验证码
        redisTemplate.delete(redisKey);
        //校验手机号是否已经存在
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("mobile", phone);
        User user = userMapper.selectOne(queryWrapper);
        User user1 = new User();
        if (null == user) {
            user1.setMobile(phone);
            user1.setPassword(DigestUtils.md5Hex("123456"));
            userMapper.insert(user1);
            isNew = true;
        }
        //生成token
        Map<String, Object> claims = new HashMap<>();
        claims.put("id", user1.getId());
        String compact = Jwts.builder()
                //payload，存放数据的位置，不能放置敏感数据，如：密码等
                .setClaims(claims)
                //设置加密方法和加密盐
                .signWith(SignatureAlgorithm.HS256, secret)
                //设置过期时间，12小时后过期
                .setExpiration(new DateTime().plusHours(12).toDate())
                .compact();
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("token", compact);
        hashMap.put("isNew", isNew);
        return hashMap;
    }
}
