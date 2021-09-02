package com.tanhua.dubbo.server.api;


import com.alibaba.dubbo.config.annotation.Reference;
import com.tanhua.dubbo.server.pojo.RecommendUser;
import com.tanhua.server.service.RecommendUserService;
import com.tanhua.server.vo.TodayBest;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;



@SpringBootTest
public class TestRecommendUserApi {

    @Reference
    private RecommendUserApi recommendUserApi;

    @Test
    public void testQueryWithMaxScore() {
        RecommendUser recommendUser = recommendUserApi.queryWithMaxScore(1l);
        System.out.println(recommendUser);
    }
}