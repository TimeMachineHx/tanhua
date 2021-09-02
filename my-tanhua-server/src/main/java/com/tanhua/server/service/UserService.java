package com.tanhua.server.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tanhua.common.pojo.User;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

/**
 * @program: my-tanhua
 * @description:
 * @author: HongXin
 * @create: 2021-08-30 21:23
 */

@Service
@Slf4j
public class UserService {
    @Autowired
    /**
     * 用于http请求
     */
    private RestTemplate restTemplate;

    @Value("${tanhua.sso.url}")
    private String ssoUrl;

    /**
     * 解析json
     */
    private static final ObjectMapper MAPPER = new ObjectMapper();

    /**
     * 通过sso的rest接口查询
     *
     * @param token
     * @return
     */
    public User queryUserByToken(String token) {
        String url = ssoUrl + "/user/" + token;
        try {
            String data = this.restTemplate.getForObject(url, String.class);
            if (StringUtils.isEmpty(data)) {
                return null;
            }
            return MAPPER.readValue(data, User.class);
        } catch (Exception e) {
            log.error("校验token出错，token = " + token, e);
        }
        return null;
    }
}


