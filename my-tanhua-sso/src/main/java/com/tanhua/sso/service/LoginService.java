package com.tanhua.sso.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.tanhua.sso.mapper.UserMapper;
import com.tanhua.sso.pojo.User;
import com.tanhua.sso.vo.RedisKey;
import io.jsonwebtoken.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * @program: my-tanhua
 * @description:
 * @author: HongXin
 * @create: 2021-08-27 21:02
 */

@Service
@Slf4j
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
        String redisKey = RedisKey.CODE + phone;
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

    /**
     * 破解token
     *
     * @param token
     * @return
     */
    public User queryUserByToken(String token) {
        try {
            Map<String, Object> body = Jwts.parser()
                    .setSigningKey(secret)
                    .parseClaimsJws(token)
                    .getBody();
            User user = new User();
            user.setId(Long.valueOf(body.get("id").toString()));
            //需要返回user对象中的mobile，需要查询数据库获取到mobile数据
            //如果每次都查询数据库，必然会导致性能问题，需要对用户的手机号进行缓存操作
            //数据缓存时，需要设置过期时间，过期时间要与token的时间一致
            //如果用户修改了手机号，需要同步修改redis中的数据
            String redisKey = RedisKey.PHONE_CODE + user.getId();
            if (Boolean.TRUE.equals(redisTemplate.hasKey(redisKey))) {
                String phone = redisTemplate.opsForValue().get(redisKey);
                user.setMobile(phone);
            } else {
                User user1 = userMapper.selectById(user.getId());
                user.setMobile(user1.getMobile());
                Long time = Long.valueOf(body.get("exp").toString()) * 1000 - System.currentTimeMillis();
                redisTemplate.opsForValue().set(redisKey, user.getMobile(), time, TimeUnit.MILLISECONDS);
            }
            return user;
        } catch (ExpiredJwtException e) {
            log.info("token已过期");
        } catch (Exception e) {
            log.error("token不合法！ token = "+ token, e);
        }
        return null;
    }
}
