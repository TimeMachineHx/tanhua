package com.tanhua.dubbo.server.service;

import cn.hutool.core.collection.CollUtil;
import com.tanhua.dubbo.server.pojo.TimeLine;
import com.tanhua.dubbo.server.pojo.Users;
import lombok.extern.slf4j.Slf4j;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.CompletableFuture;

/**
 * @program: my-tanhua
 * @description:
 * @author: HongXin
 * @create: 2021-09-03 20:16
 */

@Service
@Slf4j
public class TimeLineService {

    @Autowired
    private MongoTemplate mongoTemplate;

    @Async //异步处理，底层开一个线程执行该方法
    public CompletableFuture<String> saveTimeLine(Long userId, ObjectId publishId) {
        //写入好友时间线列表


        //查询好友列表
        try {
            Query query = Query.query(Criteria.where("userId").is(userId));
            List<Users> usersList = mongoTemplate.find(query, Users.class);
            if (CollUtil.isEmpty(usersList)) {
                return CompletableFuture.completedFuture("ok");
            }
            //一次写入好友列表
            for (Users users : usersList) {
                TimeLine timeLine = new TimeLine();
                timeLine.setId(ObjectId.get());
                timeLine.setDate(System.currentTimeMillis());
                timeLine.setPublishId(publishId);
                timeLine.setUserId(userId);
                //写⼊数据
                this.mongoTemplate.save(timeLine, "quanzi_time_line_" +
                        users.getFriendId());
            }
        } catch (Exception e) {
            log.error("写⼊好友时间线表失败~ userId = " + userId + ", publishId = " + publishId, e);
            //TODO 事务回滚问题
            return CompletableFuture.completedFuture("error");
        }
        return CompletableFuture.completedFuture("ok");

    }
}
