package com.tanhua.server.interceptor;

import com.tanhua.common.pojo.User;
import com.tanhua.common.utils.NoAuthorization;
import com.tanhua.common.utils.UserThreadLocal;
import com.tanhua.server.service.UserService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @program: my-tanhua
 * @description: 检验token
 * @author: HongXin
 * @create: 2021-09-03 10:14
 */

@Component
public class UserTokenInterceptor implements HandlerInterceptor {

    @Autowired
    private UserService userService;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        //校验handler是否是handlerMethod
        if(!(handler instanceof HandlerMethod)){
            return true;
        }
        //判断是否包含@NoAuthorization注解，如果包含，直接放⾏
        if(((HandlerMethod) handler).hasMethodAnnotation(NoAuthorization.class)){
            return true;
        }
        //从请求头中获取token
        String authorization = request.getHeader("Authorization");
        if(StringUtils.isNotEmpty(authorization)){
            User user = userService.queryUserByToken(authorization);
            if(null != user){
                //token有效
                UserThreadLocal.set(user);
                return true;
            }
        }
        //token无效，响应状态为401
        response.setStatus(401);
        return false;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
          UserThreadLocal.remove();
    }
}
