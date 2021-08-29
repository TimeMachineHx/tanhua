package com.tanhua.sso.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.tanhua.sso.enums.SexEnum;
import com.tanhua.sso.mapper.UserInfoMapper;
import com.tanhua.sso.pojo.User;
import com.tanhua.sso.pojo.UserInfo;
import com.tanhua.sso.service.LoginService;
import com.tanhua.sso.service.PicUploadService;
import com.tanhua.sso.utils.FaceEngineUtils;
import com.tanhua.sso.utils.OssUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * @program: my-tanhua
 * @description: 新用户注册
 * @author: HongXin
 * @create: 2021-08-28 22:01
 */

@Service
public class userInfoService {

    @Autowired
    private LoginService loginService;
    @Autowired
    private UserInfoMapper userInfoMapper;
    @Autowired
    private FaceEngineUtils faceEngineUtils;
    @Autowired
    PicUploadService uploadService;
    public boolean saveUserInfo(Map<String, String> param, String token) {
        //验证token是否合法
        User user = loginService.queryUserByToken(token);
        if (user == null) {
            return false;
        }
        UserInfo userInfo = new UserInfo();
        userInfo.setUserId(user.getId());
        userInfo.setSex(StringUtils.equalsIgnoreCase(param.get("gender"), "man") ? SexEnum.MAN : SexEnum.WOMAN);
        userInfo.setNickName(param.get("nickname"));
        userInfo.setBirthday(param.get("birthday"));
        userInfo.setCity(param.get("city"));
        return this.userInfoMapper.insert(userInfo) == 1;
    }

    /**
     * 上传头像
     *
     * @param file
     * @param token
     * @return
     */
    public Boolean saveUserLogo(MultipartFile file, String token) throws IOException {
        User user = loginService.queryUserByToken(token);
        if (null == user) {
            return false;
        }
        //判断头像是否合法
        boolean b = false;
        try {
            b = faceEngineUtils.checkIsPortrait(file.getBytes());
            if (!b) {
                return false;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
       //头像进行上传
        try {
            ResponseEntity<Object> upload = uploadService.upload(file);
            //上传失败
            if(upload == null){
                return false;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        //把头像保存到用户信息表中
        UserInfo userInfo = new UserInfo();
        HashMap<String,String> map = (HashMap<String, String>) uploadService.upload(file).getBody();
        userInfo.setLogo(map.get("name"));

        QueryWrapper<UserInfo> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_id", user.getId());

        return this.userInfoMapper.update(userInfo, queryWrapper) == 1;
    }

}
