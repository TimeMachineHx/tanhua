package com.tanhua.dubbo.server.api;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.PageUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.dubbo.config.annotation.Service;
import com.tanhua.dubbo.server.enums.IdType;
import com.tanhua.dubbo.server.pojo.Album;
import com.tanhua.dubbo.server.pojo.Publish;
import com.tanhua.dubbo.server.pojo.TimeLine;
import com.tanhua.dubbo.server.service.IdService;
import com.tanhua.dubbo.server.service.TimeLineService;
import com.tanhua.dubbo.server.vo.PageInfo;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.redis.core.RedisTemplate;

import java.util.ArrayList;
import java.util.List;


/**
 * @program: my-tanhua
 * @description:
 * @author: HongXin
 * @create: 2021-09-02 20:28
 */

@Service(version = "1.0.0")
public class QuanZiApiImpl implements QuanZiApi {
    @Autowired
    private MongoTemplate mongoTemplate;
    @Autowired
    private IdService idService;

    @Autowired
    private TimeLineService timeLineService;

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    /**
     * 查询好友动态
     *
     * @param userId   ⽤户id
     * @param page     当前⻚数
     * @param pageSize 每⼀⻚查询的数据条数
     * @return
     */
    @Override
    public PageInfo<Publish> queryPublishList(Long userId, Integer page, Integer pageSize) {
        //查询好友动态，就是查询时间线表
        PageInfo<Publish> pageInfo = new PageInfo<>();
        pageInfo.setPageNum(page);
        pageInfo.setPageSize(pageSize);
        PageRequest date = PageRequest.of(page - 1, pageSize, Sort.by(Sort.Order.desc("date")));
        Query query = new Query().with(date);
        List<TimeLine> timeLines = mongoTemplate.find(query, TimeLine.class, "quanzi_time_line_" + userId);
        if (CollUtil.isEmpty(timeLines)) {
            //没有查询到数据
            return pageInfo;
        }
        //获取时间线列表中的发布id列表
        List<Object> publishId = CollUtil.getFieldValues(timeLines, "publishId");
        //根据动态id查询动态列表
        Query query1 = Query.query(Criteria.where("id").in(publishId)).with(Sort.by(Sort.Order.desc("created")));
        List<Publish> publishes = mongoTemplate.find(query1, Publish.class);
        pageInfo.setRecords(publishes);
        return pageInfo;
    }


    /**
     * 发布动态
     *
     * @param publish
     * @return 发布成功返回动态id
     */
    @Override
    public String savePublish(Publish publish) {
        //对publish进行校验
        if (!ObjectUtil.isAllNotEmpty(publish.getText(), publish.getUserId())) {
            //发布失败
            return null;
        }
        //设置主键id
        publish.setId(ObjectId.get());
        //设置自增长的pid
        publish.setPid(idService.createId(IdType.PUBLISH));
        publish.setCreated(System.currentTimeMillis());
        //写入publish表
        mongoTemplate.save(publish);
        //写入相册表
        Album album = new Album();
        album.setId(ObjectId.get());
        album.setCreated(System.currentTimeMillis());
        album.setPublishId(publish.getId());
        mongoTemplate.save(album, "quanzi_album_" + publish.getUserId());
        //写入好友时间线表（异步写入）
        timeLineService.saveTimeLine(publish.getUserId(), publish.getId());

        return publish.getId().toHexString();
    }

    @Override
    public PageInfo<Publish> queryRecommendPublishList(Long userId, Integer page, Integer pageSize) {
        PageInfo<Publish> pageInfo = new PageInfo<>();
        pageInfo.setPageNum(page);
        pageInfo.setPageSize(pageSize);
        // 查询推荐结果数据
        String key = "QUANZI_PUBLISH_RECOMMEND_" + userId;
        String data = this.redisTemplate.opsForValue().get(key);
        if (StrUtil.isEmpty(data)) {
            return pageInfo;
        }
        //查询到的pid进⾏分⻚处理

        List<String> pids = StrUtil.split(data, ',');
        //计算分⻚
        //[0, 10]
        int[] startEnd = PageUtil.transToStartEnd(page - 1, pageSize);
        int startIndex = startEnd[0]; //开始
        int endIndex = Math.min(startEnd[1], pids.size()); //结束
        List<Long> pidLongList = new ArrayList<>();
        for (int i = startIndex; i < endIndex; i++) {
            pidLongList.add(Long.valueOf(pids.get(i)));
        }
        if (CollUtil.isEmpty(pidLongList)) {
            //没有查询到数据
            return pageInfo;
        }
        //根据pid查询publish
        Query query = Query.query(Criteria.where("pid").in(pidLongList))
                .with(Sort.by(Sort.Order.desc("created")));
        List<Publish> publishList = this.mongoTemplate.find(query,
                Publish.class);
        if (CollUtil.isEmpty(publishList)) {
            //没有查询到数据
            return pageInfo;
        }
        pageInfo.setRecords(publishList);
        return pageInfo;
    }

}
