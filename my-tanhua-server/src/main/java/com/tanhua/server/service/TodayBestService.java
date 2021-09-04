package com.tanhua.server.service;

import com.alibaba.dubbo.common.utils.CollectionUtils;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.tanhua.common.pojo.User;
import com.tanhua.common.pojo.UserInfo;
import com.tanhua.common.utils.UserThreadLocal;
import com.tanhua.dubbo.server.pojo.RecommendUser;
import com.tanhua.dubbo.server.vo.PageInfo;
import com.tanhua.server.dto.RecommendUserQueryParam;
import com.tanhua.server.vo.PageResult;
import com.tanhua.server.vo.TodayBest;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * @program: my-tanhua
 * @description:
 * @author: HongXin
 * @create: 2021-08-30 20:55
 */

@Service
public class TodayBestService {
    @Autowired
    private UserService userService;

    @Autowired
    private RecommendUserService recommendUserService;

    @Autowired
    private userInfoService userInfoService;

    @Value("${tanhua.sso.default.user}")
    private Long defaultUser;

    public TodayBest queryTodayBest() {
//        //校验token。通过sso接口进行校验
//        User user = userService.queryUserByToken(token);
//        if (null == user) {
//            //token过期或非法
//            return null;
//        }
        //查询用户
        User user = UserThreadLocal.get();
        //查出推荐用户
        TodayBest todayBest = recommendUserService.queryTodayBest(user.getId());
        //判断是否存在推荐用户，没有的话，给一个默认用户
        if (null == todayBest) {
            todayBest.setId(defaultUser);
            todayBest.setFateValue(80D);
        }
        //补全个人信息
        UserInfo userInfo = userInfoService.queryUserInfoByUserId(todayBest.getId());
        todayBest.setAge(userInfo.getAge());
        todayBest.setAvatar(userInfo.getLogo());
        todayBest.setNickname(userInfo.getNickName());
        todayBest.setTags(StringUtils.split(userInfo.getTags(), ","));
        todayBest.setGender(userInfo.getSex().getValue() == 1 ? "man" : "woman");
        return todayBest;
    }


    /**
     * 查询推荐用户
     *
     *
     * @param queryParam
     * @return
     */
    public PageResult queryRecommendation(RecommendUserQueryParam queryParam) {
//        User user = userService.queryUserByToken(token);
//        if (null == user) {
//            return null;
//        }
        User user = UserThreadLocal.get();
        PageResult pageResult = new PageResult();
        pageResult.setPage(queryParam.getPage());
        pageResult.setPagesize(queryParam.getPagesize());
        PageInfo<RecommendUser> pageInfo = recommendUserService.queryRecommendUserList(user.getId(), queryParam.getPage(), queryParam.getPagesize());
        List<RecommendUser> records = pageInfo.getRecords();
        if (null == pageInfo) {
            return pageResult;
        }
        //填充个人信息

        //手机推荐用户id
        Set<Long> userIds = new HashSet<>();
        for (RecommendUser record : records) {
            userIds.add(record.getUserId());
        }
        QueryWrapper<UserInfo> queryWrapper = new QueryWrapper<>();

        //用户id参数
        queryWrapper.in("user_id", userIds);

//        if (StringUtils.isNotEmpty(queryParam.getGender())) {
////            queryWrapper.eq("sex", StringUtils.equals(queryParam.getGender(), "man") ? 1 : 2);
//        }
//        if (StringUtils.isNotEmpty(queryParam.getCity())) {
//            //需要城市参数查询
////            queryWrapper.like("city", queryParam.getCity());
//        }
//
//        if (queryParam.getAge() != null) {
//            //设置年龄参数，条件：小于等于
////            queryWrapper.le("age", queryParam.getAge());
//        }

        List<UserInfo> userInfoList = this.userInfoService.queryUserInfoList(queryWrapper);
        if (CollectionUtils.isEmpty(userInfoList)) {
            //没有查询到用户的基本信息
            return pageResult;
        }

        List<TodayBest> todayBests = new ArrayList<>();
        for (UserInfo userInfo : userInfoList) {
            TodayBest todayBest = new TodayBest();

            todayBest.setId(userInfo.getUserId());
            todayBest.setAvatar(userInfo.getLogo());
            todayBest.setNickname(userInfo.getNickName());
            todayBest.setTags(StringUtils.split(userInfo.getTags(), ','));
            todayBest.setGender(userInfo.getSex().getValue() == 1 ? "man" : "woman");
            todayBest.setAge(userInfo.getAge());

            //缘分值
            for (RecommendUser record : records) {
                if (record.getUserId().longValue() == userInfo.getUserId().longValue()) {
                    double score = Math.floor(record.getScore());//取整,98.2 -> 98
                    todayBest.setFateValue(score);
                    break;
                }
            }

            todayBests.add(todayBest);
        }

        //按照缘分值进行倒序排序
        todayBests.sort((o1, o2) -> new Double(o2.getFateValue() - o1.getFateValue()).intValue());

        pageResult.setItems(todayBests);

        return pageResult;
    }
}

