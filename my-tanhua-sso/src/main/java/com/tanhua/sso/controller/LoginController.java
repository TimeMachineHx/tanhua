package com.tanhua.sso.controller;

import com.tanhua.sso.pojo.User;
import com.tanhua.sso.service.LoginService;
import com.tanhua.sso.vo.ErrorResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * @program: my-tanhua
 * @description: 登录
 * @author: HongXin
 * @create: 2021-08-27 20:55
 */
@RestController
@RequestMapping("user")
@Slf4j
public class LoginController {
    @Autowired
    private LoginService loginService;

    @PostMapping("loginVerification")
    public ResponseEntity<Object> loginVerification(@RequestBody Map<String, String> param) {
        String verificationCode = param.get("verificationCode");
        String phone = param.get("phone");
        Map<String, Object> stringObjectMap = loginService.loginVerification(verificationCode, phone);
        //如果等于空，返回错误状态
        if (stringObjectMap == null) {
            ErrorResult result = ErrorResult.builder().errCode("000001").errMessage("验证码错误").build();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(result);
        }
        return ResponseEntity.status(HttpStatus.OK).body(stringObjectMap);
    }

    /**
     * 校验token，根据token查询用户数据
     *
     * @param token
     * @return
     */
    @GetMapping("{token}")
    public User queryUserByToken(@PathVariable("token") String token) {
        return this.loginService.queryUserByToken(token);
    }
}
