package com.tanhua.server.interceptor;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tanhua.common.utils.Cache;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @program: my-tanhua
 * @description: 拦截器
 * @author: HongXin
 * @create: 2021-09-02 15:10
 */

@Component
public class RedisCacheInterceptor implements HandlerInterceptor {
    public static String createRedisKey;
    //是否开启缓存
    @Value("${tanhua.cache.enable}")
    private Boolean enable;

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    private static final ObjectMapper MAPPER = new ObjectMapper();

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        //缓存的全局开关校验
        if (!enable) {
            return true;
        }

        //校验handler是否是handlerMethod
        if (!(handler instanceof HandlerMethod)) {
            return true;
        }

        //判断是否为get请求
        if (!((HandlerMethod) handler).hasMethodAnnotation(GetMapping.class)) {
            return true;
        }

        //判断是否使用@cache注解
        if (!(((HandlerMethod) handler).hasMethodAnnotation(Cache.class))) {
            return true;
        }

        //缓存命中
        String redisKey = createRedisKey(request);
        String cacheData = redisTemplate.opsForValue().get(redisKey);
        if (StringUtils.isEmpty(cacheData)) {
            //未能命中
            return true;
        }
        response.setCharacterEncoding("utf-8");
        response.setContentType("application/json;charset=utf-8");
        response.getWriter().write(cacheData);
        return false;
    }


    /**
     * 生成redis中的key，规则：SERVER_CACHE_DATA_MD5(url + param + token)
     *
     * @param request
     * @return
     */

    public static String createRedisKey(HttpServletRequest request) throws JsonProcessingException {
        String url = request.getRequestURI();
        String param = MAPPER.writeValueAsString(request.getParameterMap());
        String token = request.getHeader("Authorization");
        String data = url + param + token;
        return "SERVER_CACHE_DATA_" + DigestUtils.md5Hex(data);
    }
}
