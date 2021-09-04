package com.tanhua.dubbo.server.api;

import com.tanhua.dubbo.server.pojo.Publish;
import com.tanhua.dubbo.server.vo.PageInfo;

/**
 * @program: my-tanhua
 * @description:
 * @author: HongXin
 * @create: 2021-09-02 20:23
 */



public interface QuanZiApi {
    /**
     * 查询好友动态
     4.2.2、实现接⼝
     在my-tanhua-dubbo-service中完成：
     *
     * @param userId ⽤户id
     * @param page 当前⻚数
     * @param pageSize 每⼀⻚查询的数据条数
     * @return
     */
    PageInfo<Publish>  queryPublishList(Long userId, Integer page, Integer
            pageSize);


    /**
     * 发布动态，返回动态id
     * @param publish
     * @return
     */
    String savePublish(Publish publish);



    /**
     * 查询推荐动态
     *
     * @param userId ⽤户id
     * @param page 当前⻚数
     * @param pageSize 每⼀⻚查询的数据条数
     * @return
     */
    PageInfo<Publish> queryRecommendPublishList(Long userId, Integer page,
                                                Integer pageSize);
}
