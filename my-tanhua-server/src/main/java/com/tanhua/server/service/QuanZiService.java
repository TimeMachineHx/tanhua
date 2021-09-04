package com.tanhua.server.service;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import com.alibaba.dubbo.config.annotation.Reference;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.tanhua.common.pojo.User;
import com.tanhua.common.pojo.UserInfo;
import com.tanhua.common.service.PicUploadService;
import com.tanhua.common.utils.RelativeDateFormat;
import com.tanhua.common.utils.UserThreadLocal;
import com.tanhua.dubbo.server.api.QuanZiApi;
import com.tanhua.dubbo.server.pojo.Publish;
import com.tanhua.dubbo.server.vo.PageInfo;
import com.tanhua.server.vo.PageResult;
import com.tanhua.server.vo.QuanZiVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * @program: my-tanhua
 * @description:
 * @author: HongXin
 * @create: 2021-09-02 23:13
 */

@Service
public class QuanZiService {
    @Reference(version = "1.0.0")
    private QuanZiApi quanZiApi;

    @Autowired
    private PicUploadService picUploadService;
    @Autowired
    private userInfoService userInfoService;

    public PageResult queryPublishList(Integer page, Integer pageSize) {
        if (page < 1) {
            page = 1;
        }
        if (pageSize < 1) {
            pageSize = 10;
        }
        PageResult pageResult = new PageResult();
        pageResult.setPage(page);
        pageResult.setPagesize(pageSize);
        //获取user对象
        User user = UserThreadLocal.get();
//        User user = userService.queryUserByToken(token);
//        if (null == user) {
//            //token已失效
//            return pageResult;
//        }
        //通过dubbp查询数据
        PageInfo<Publish> pageInfo = quanZiApi.queryPublishList(user.getId(), page, pageSize);
        List<Publish> records = pageInfo.getRecords();
        if (CollUtil.isEmpty(records)) {
            return pageResult;
        }

        List<QuanZiVo> quanZiVos = new ArrayList<>();
        records.forEach(publish -> {
            QuanZiVo quanZiVo = new QuanZiVo();
            quanZiVo.setId(publish.getId().toHexString());
            quanZiVo.setTextContent(publish.getText());
            quanZiVo.setImageContent(publish.getMedias().toArray(new String[]{}));
            quanZiVo.setUserId(publish.getUserId());
            quanZiVo.setCreateDate(RelativeDateFormat.format(new Date(publish.getCreated())));
            quanZiVos.add(quanZiVo);
        });
        List<Object> userId = CollUtil.getFieldValues(quanZiVos, "userId");
        List<UserInfo> userInfoList = userInfoService.queryUserInfoByUserIdList(userId);
        for (QuanZiVo quanZiVo : quanZiVos) {
            //找到对应的用户信息
            for (UserInfo userInfo : userInfoList) {
                if (quanZiVo.getUserId().longValue() ==
                        userInfo.getUserId().longValue()) {
                    this.fillUserInfoToQuanZiVo(userInfo, quanZiVo);
                    break;
                }
            }
        }
        pageResult.setItems(quanZiVos);
        return pageResult;
    }




    public String savePublish(String textContent, String location, String latitude, String longitude, MultipartFile[] multipartFile) throws IOException {
        //查询当前的登录信息
        User user = UserThreadLocal.get();
        Publish publish = new Publish();
        publish.setUserId(user.getId());
        publish.setText(textContent);
        publish.setLocationName(location);
        publish.setLatitude(latitude);
        publish.setLongitude(longitude);
        publish.setSeeType(1);
        List<String> picUrls = new ArrayList<>();
        //图⽚上传
        for (MultipartFile file : multipartFile) {
//            PicUploadResult picUploadResult =
            ResponseEntity<Object> upload = this.picUploadService.upload(file);
            Map<String, String> map = (Map<String, String>) upload.getBody();
            picUrls.add(map.get("name"));
        }
        publish.setMedias(picUrls);
        return this.quanZiApi.savePublish(publish);
    }


    public PageResult queryRecommendPublishList(Integer page, Integer pageSize) {
        //分析：通过dubbo中的服务查询系统推荐动态
        //通过mysql查询⽤户的信息，回写到结果对象中（QuanZiVo）
        PageResult pageResult = new PageResult();
        pageResult.setPage(page);
        pageResult.setPagesize(pageSize);
        //直接从ThreadLocal中获取对象
        User user = UserThreadLocal.get();
        //通过dubbo查询数据
        PageInfo<Publish> pageInfo =
                this.quanZiApi.queryRecommendPublishList(user.getId(), page, pageSize);
        List<Publish> records = pageInfo.getRecords();
        if (CollUtil.isEmpty(records)) {
            return pageResult;
        }
        pageResult.setItems(this.fillQuanZiVo(records));
        return pageResult;
    }

    /**
     * 根据查询到的publish集合填充QuanZiVo对象
     *
     * @param records
     * @return
     */
    private List<?> fillQuanZiVo(List<Publish> records) {
        List<QuanZiVo> quanZiVoList = new ArrayList<>();
        records.forEach(publish -> {
            QuanZiVo quanZiVo = new QuanZiVo();
            quanZiVo.setId(publish.getId().toHexString());
            quanZiVo.setTextContent(publish.getText());
            quanZiVo.setImageContent(publish.getMedias().toArray(new String[]
                    {}));
            quanZiVo.setUserId(publish.getUserId());
            quanZiVo.setCreateDate(RelativeDateFormat.format(new
                    Date(publish.getCreated())));
            quanZiVoList.add(quanZiVo);
        });
        //查询⽤户信息
        List<Object> userIds = CollUtil.getFieldValues(records, "userId");
        List<UserInfo> userInfoList =
                this.userInfoService.queryUserInfoByUserIdList(userIds);
        for (QuanZiVo quanZiVo : quanZiVoList) {
            //找到对应的⽤户信息
            for (UserInfo userInfo : userInfoList) {
                if (quanZiVo.getUserId().longValue() ==
                        userInfo.getUserId().longValue()) {
                    this.fillUserInfoToQuanZiVo(userInfo, quanZiVo);
                    break;
                }
            }
        }
        return quanZiVoList;
    }




    /**
     * 填充⽤户信息
     *
     * @param userInfo
     * @param quanZiVo
     */
    private void fillUserInfoToQuanZiVo(UserInfo userInfo, QuanZiVo quanZiVo) {
        BeanUtil.copyProperties(userInfo, quanZiVo, "id");
        quanZiVo.setGender(userInfo.getSex().name().toLowerCase());
        quanZiVo.setTags(StringUtils.split(userInfo.getTags(), ","));
        quanZiVo.setCommentCount(0); //TODO 评论数
        quanZiVo.setDistance("1.2公⾥"); //TODO 距离
        quanZiVo.setHasLiked(0); //TODO 是否点赞（1是，0否）
        quanZiVo.setLikeCount(0); //TODO 点赞数
        quanZiVo.setHasLoved(0); //TODO 是否喜欢（1是，0否）
        quanZiVo.setLoveCount(0); //TODO 喜欢数
    }
}
