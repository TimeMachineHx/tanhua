package com.tanhua.server.service;

import com.alibaba.dubbo.config.annotation.Reference;
import com.tanhua.dubbo.server.api.RecommendUserApi;
import com.tanhua.dubbo.server.pojo.RecommendUser;
import com.tanhua.dubbo.server.vo.PageInfo;
import com.tanhua.server.vo.TodayBest;
import org.springframework.stereotype.Service;

/**
 * @program: my-tanhua
 * @description: 与dubbo服务交换
 * @author: HongXin
 * @create: 2021-08-30 23:42
 */

@Service
public class RecommendUserService {
    @Reference(version = "1.0.0")
    private RecommendUserApi recommendUserApi;

    public TodayBest queryTodayBest(Long userId) {
        RecommendUser recommendUser = recommendUserApi.queryWithMaxScore(userId);
        if (null == recommendUser) {
            return null;
        }
        TodayBest todayBest = new TodayBest();
        todayBest.setId(recommendUser.getUserId());
        todayBest.setFateValue(recommendUser.getScore());
        return todayBest;
    }

    public PageInfo<RecommendUser> queryRecommendUserList(Long id, Integer page, Integer pagesize) {
        return recommendUserApi.queryPageInfo(id, page, pagesize);
    }
}
